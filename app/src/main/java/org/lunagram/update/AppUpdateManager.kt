package org.lunagram.update

import io.ktor.client.HttpClient
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.lunagram.core.common.AppLog
import org.lunagram.core.common.Outcome
import org.lunagram.core.common.SponsorRegistry
import org.lunagram.core.models.AppUpdate
import org.lunagram.core.models.AppUpdateInfo
import org.lunagram.core.models.AppUpdateState
import org.lunagram.core.models.Message
import org.lunagram.core.models.MessageId
import org.lunagram.core.models.PeerId
import org.lunagram.core.models.rememberHiddenInternalChat
import org.lunagram.feature.settings.AppUpdateController
import org.lunagram.network.http.HttpModule

class AppUpdateManager(
    private val scope: CoroutineScope,
    private val isAuthorized: suspend () -> Boolean,
    private val betaUpdates: () -> Boolean,
    private val isSupporter: suspend () -> Boolean,
    private val resolveUsername: suspend (String) -> Outcome<Long>,
    private val getHistoryPage: suspend (Long, Int, Int) -> Outcome<List<Message>>,
    private val telegramDownload: suspend (Message) -> Outcome<File>,
    private val cancelDownloadKey: (String) -> Unit,
    private val progress: StateFlow<Map<String, Long>>,
    private val currentVersionCode: () -> Int,
    private val currentCommit: () -> String,
    private val buildType: String,
    private val supportedAbis: () -> List<String>,
    private val installer: AppUpdateInstaller,
    private val updateDir: File,
    private val httpClient: HttpClient = HttpModule.createClient(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AppUpdateController {
    private val _state = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)
    override val state: StateFlow<AppUpdateState> = _state.asStateFlow()

    private var downloadJob: Job? = null
    private var progressJob: Job? = null
    private var currentInfo: AppUpdateInfo? = null
    private val started = AtomicBoolean(false)

    fun start() {
        if (!AppUpdate.inAppUpdatesEnabled(buildType)) {
            AppLog.api(TAG, "check skipped debug")
            return
        }
        if (!started.compareAndSet(false, true)) return
        scope.launch(ioDispatcher) {
            // 定期检查更新（不再依赖赞助者状态）
            while (isActive) {
                checkForUpdatesInternal()
                if (checkSettled()) break
                delay(AUTH_CHECK_INTERVAL_MS)
            }
        }
    }

    override fun checkForUpdates() {
        if (!AppUpdate.inAppUpdatesEnabled(buildType)) return
        scope.launch(ioDispatcher) { checkForUpdatesInternal() }
    }

    override fun downloadUpdate() {
        if (!AppUpdate.inAppUpdatesEnabled(buildType)) return
        if (_state.value is AppUpdateState.Installing) return
        val info = when (val current = _state.value) {
            is AppUpdateState.Available -> current.info
            is AppUpdateState.Error -> current.info ?: currentInfo
            else -> currentInfo
        } ?: return
        currentInfo = info
        _state.value = AppUpdateState.Downloading(info, 0L)
        progressJob?.cancel()
        downloadJob?.cancel()
        if (info.downloadUrl != null) {
            downloadJob = scope.launch(ioDispatcher) {
                val zip = File(updateDir, "artifact.zip")
                when (
                    val result = downloadHttpApk(httpClient, info.downloadUrl!!, zip) { bytes ->
                        val current = _state.value
                        if (current is AppUpdateState.Downloading || current is AppUpdateState.Available) {
                            _state.value = AppUpdateState.Downloading(info, bytes)
                        }
                    }
                ) {
                    is Outcome.Ok -> {
                        // 你的 Release 附件是直接下载的 APK，不是 zip
                        val apk = File(updateDir, "artifact.apk")
                        zip.renameTo(apk)
                        _state.value = AppUpdateState.ReadyToInstall(info, apk.absolutePath)
                    }
                    is Outcome.Err ->
                        _state.value = if (result.message == "cancelled") {
                            AppUpdateState.Available(info)
                        } else {
                            AppUpdateState.Error(result.message, info)
                        }
                }
            }
            return
        }
        progressJob = scope.launch(ioDispatcher) {
            progress.collect { map ->
                val bytes = map[info.mediaCacheKey] ?: return@collect
                val current = _state.value
                if (current is AppUpdateState.Downloading || current is AppUpdateState.Available) {
                    _state.value = AppUpdateState.Downloading(info, bytes)
                }
            }
        }
        downloadJob = scope.launch(ioDispatcher) {
            when (val result = telegramDownload(info.toMessage())) {
                is Outcome.Ok -> {
                    progressJob?.cancel()
                    _state.value = AppUpdateState.ReadyToInstall(info, result.value.absolutePath)
                }
                is Outcome.Err -> {
                    progressJob?.cancel()
                    _state.value = if (result.message == "cancelled") {
                        AppUpdateState.Available(info)
                    } else {
                        AppUpdateState.Error(result.message, info)
                    }
                }
            }
        }
    }

    override fun cancelDownload() {
        val info = currentInfo ?: return
        if (info.downloadUrl == null) cancelDownloadKey(info.mediaCacheKey)
        downloadJob?.cancel()
        progressJob?.cancel()
        _state.value = AppUpdateState.Available(info)
    }

    override fun installUpdate() {
        if (!AppUpdate.inAppUpdatesEnabled(buildType)) return
        val ready = _state.value as? AppUpdateState.ReadyToInstall ?: return
        val installing = AppUpdate.beginInstall(ready) as AppUpdateState.Installing
        _state.value = installing
        UpdateInstallBridge.onStatus = { status -> onInstallStatus(status) }
        UpdateInstallBridge.onHandoffEnded = { onExternalHandoffEnded(installing) }
        val launch = when (installer.install(File(ready.filePath))) {
            AppUpdateInstallLaunch.SessionStarted -> AppUpdate.InstallLaunch.Session
            AppUpdateInstallLaunch.NeedsUserPermission,
            AppUpdateInstallLaunch.ExternalConfirm -> AppUpdate.InstallLaunch.ExternalHandoff
            AppUpdateInstallLaunch.Failed -> AppUpdate.InstallLaunch.Failed
        }
        if (launch != AppUpdate.InstallLaunch.ExternalHandoff) {
            UpdateInstallBridge.onHandoffEnded = null
        }
        if (_state.value == installing) {
            _state.value = AppUpdate.stateAfterInstallLaunch(installing, launch)
        }
    }

    private fun onExternalHandoffEnded(installing: AppUpdateState.Installing) {
        val current = _state.value as? AppUpdateState.Installing ?: return
        if (current.filePath != installing.filePath) return
        _state.value = AppUpdate.afterExternalHandoffEnded(current)
    }

    private fun onInstallStatus(status: Int) {
        val installing = _state.value as? AppUpdateState.Installing ?: return
        val kind = AppUpdate.installStatusKind(status)
        _state.value = AppUpdate.afterInstallStatus(installing, kind)
    }

    private suspend fun checkForUpdatesInternal() {
        if (_state.value is AppUpdateState.Installing) return
        if (!AppUpdate.inAppUpdatesEnabled(buildType)) {
            AppLog.api(TAG, "check skipped debug")
            _state.value = AppUpdateState.Idle
            return
        }
        val localCode = currentVersionCode()
        val localCommit = currentCommit()
        val abis = supportedAbis()
        val betaPref = betaUpdates()
        
        AppLog.api(
            TAG,
            "check source=github local=$localCode commit=$localCommit type=$buildType abis=${abis.joinToString()} beta=$betaPref",
        )
        checkGitHub()
    }

    private suspend fun checkGitHub() {
        _state.value = AppUpdateState.Checking
        val releases = when (val fetched = fetchGitHubReleases(httpClient)) {
            is Outcome.Ok -> fetched.value
            is Outcome.Err -> {
                AppLog.warn(TAG, "github releases list failed ${fetched.message}")
                _state.value = AppUpdateState.Error(fetched.message)
                return
            }
        }
        val betaAllowed = AppUpdate.actionsBetaAllowed(betaPref, isSupporter())
        AppLog.api(
            TAG,
            "github releases=${releases.size} beta_allowed=$betaAllowed type=$buildType",
        )
        val picked = githubReleaseUpdateInfo(releases, buildType, betaAllowed)
        AppLog.api(
            TAG,
            if (picked == null) {
                "github pick=none type=$buildType want=${AppUpdate.compatibleBuildTypes(buildType).joinToString()} beta=$betaAllowed"
            } else {
                "github pick=${picked.fileName} code=${picked.versionCode} type=${picked.buildType} bytes=${picked.fileSize}"
            },
        )
        applySelected(listOfNotNull(picked))
    }

    private fun applySelected(infos: List<AppUpdateInfo>) {
        val selected = AppUpdate.select(infos, supportedAbis(), buildType)
        val next = AppUpdate.afterCheck(
            authorized = true,
            currentVersionCode = currentVersionCode(),
            selected = selected,
            currentCommit = currentCommit(),
            currentBuildType = buildType,
        )
        if (next is AppUpdateState.Available) currentInfo = next.info
        AppLog.api(
            TAG,
            when (next) {
                is AppUpdateState.Available ->
                    "result=available ${next.info.fileName} code=${next.info.versionCode} type=${next.info.buildType}"
                AppUpdateState.UpToDate -> "result=up_to_date local=${currentVersionCode()} commit=${currentCommit()}"
                is AppUpdateState.Error -> "result=error ${next.message}"
                else -> "result=${next::class.simpleName}"
            },
        )
        _state.value = next
    }

    private fun AppUpdateInfo.toMessage(): Message = Message(
        id = MessageId(PeerId(chatId), messageId),
        senderId = null,
        text = description,
        date = 0L,
        outgoing = false,
        mediaCacheKey = mediaCacheKey,
        mediaKind = "document",
        fileName = fileName,
        fileSize = fileSize,
    )

    private suspend fun awaitSponsorLookup() {
        if (SponsorRegistry.loaded.value) return
        withTimeoutOrNull(SPONSOR_WAIT_MS) {
            SponsorRegistry.loaded.first { it }
        }
    }

    private fun checkSettled(): Boolean = when (val state = _state.value) {
        is AppUpdateState.Available,
        AppUpdateState.UpToDate,
        is AppUpdateState.Downloading,
        is AppUpdateState.ReadyToInstall,
        is AppUpdateState.Installing -> true
        is AppUpdateState.Error -> state.message == AppUpdate.NO_UPDATE
        else -> false
    }

    private companion object {
        const val TAG = "update"
        const val AUTH_CHECK_INTERVAL_MS = 5_000L
        const val SPONSOR_WAIT_MS = 5_000L
    }
}
