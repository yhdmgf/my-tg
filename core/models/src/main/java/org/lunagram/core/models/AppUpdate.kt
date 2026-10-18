package org.lunagram.core.models

data class AppUpdateInfo(
    val version: String,
    val versionCode: Int,
    val description: String,
    val changelog: List<String>,
    val fileName: String,
    val fileSize: Long,
    val abi: String,
    val buildType: String,
    val chatId: Long,
    val messageId: Int,
    val mediaCacheKey: String,
    val downloadUrl: String? = null,
    val commit: String? = null,
)

sealed interface AppUpdateState {
    data object Idle : AppUpdateState
    data object Checking : AppUpdateState
    data class Available(val info: AppUpdateInfo) : AppUpdateState
    data object UpToDate : AppUpdateState
    data class Downloading(val info: AppUpdateInfo, val bytes: Long) : AppUpdateState
    data class ReadyToInstall(val info: AppUpdateInfo, val filePath: String) : AppUpdateState
    data class Installing(val info: AppUpdateInfo, val filePath: String) : AppUpdateState
    data class Error(val message: String, val info: AppUpdateInfo? = null) : AppUpdateState
}

object AppUpdate {
    const val CHANNEL_USERNAME = "monogram_apks"
    const val FALLBACK_CHAT_ID = -1003566234286L
    const val HISTORY_LIMIT = 20
    const val NO_UPDATE = "no_update"

    /** 更新源：自己的 GitHub Releases（Lunagram） */
    const val GITHUB_RELEASES =
        "https://api.github.com/repos/yhdmgf/my-tg/releases?per_page=30"

    /** APK 文件名前缀 */
    const val APK_PREFIX = "Lunagram_android_"

    private val VERSION_IN_CAPTION = Regex("""(\d+\.\d+\.\d+)\s*\((\d+)\)""")
    private val LEADING_NUMBER = Regex("""^\d+\.\s*""")

    /**
     * 解析 Lunagram APK 文件名：
     *   Lunagram_android_2.1.1.apk       -> version=2.1.1, beta=false
     *   Lunagram_android_2.1.1_beta.apk  -> version=2.1.1, beta=true
     */
    private val LUNAGRAM_APK = Regex(
        """^Lunagram_android_(\d+(?:\.\d+)*)(?:_(beta))?\.apk$""",
        RegexOption.IGNORE_CASE,
    )

    /** 把 "2.1.1" 转成可比较的数字（2*10000 + 1*100 + 1 = 20101） */
    fun versionToCode(version: String): Int {
        val parts = version.trim().split('.')
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return major * 10000 + minor * 100 + patch
    }

    /** 解析 APK 文件名，返回 (version, isBeta) 或 null */
    fun parseLunagramApk(fileName: String): Pair<String, Boolean>? {
        val match = LUNAGRAM_APK.matchEntire(fileName.trim()) ?: return null
        val version = match.groupValues[1]
        val isBeta = match.groupValues[2].isNotBlank()
        return version to isBeta
    }

    /** 是否为 Lunagram 的 APK 附件 */
    fun isLunagramApk(fileName: String): Boolean =
        fileName.startsWith(APK_PREFIX, ignoreCase = true) &&
            fileName.endsWith(".apk", ignoreCase = true)

    /**
     * 当前安装的构建类型能接受哪些远端构建类型。
     * - 正式版：只接受正式版
     * - 测试版：接受测试版 + 正式版（同级正式版也算升级）
     */
    fun compatibleBuildTypes(installed: String): Set<String> {
        val type = installed.lowercase()
        return if (type == "beta") setOf("beta", "release") else setOf("release")
    }

    fun inAppUpdatesEnabled(buildType: String): Boolean =
        buildType.lowercase() != "debug"

    /** Beta 更新开关：所有人可用，只看用户偏好 */
    fun actionsBetaAllowed(betaPref: Boolean, isSupporter: Boolean): Boolean = betaPref

    fun commitsMatch(remote: String?, local: String?): Boolean {
        val a = remote?.trim()?.lowercase().orEmpty()
        val b = local?.trim()?.lowercase().orEmpty()
        if (a.isEmpty() || b.isEmpty() || b == "unknown") return false
        return a == b || a.startsWith(b) || b.startsWith(a)
    }

    fun parseCaptionVersion(text: String): Pair<String, Int>? {
        val match = VERSION_IN_CAPTION.find(text) ?: return null
        return match.groupValues[1] to match.groupValues[2].toInt()
    }

    fun parseChangelog(text: String): List<String> {
        val marker = text.indexOf("Changelog:", ignoreCase = true)
        if (marker < 0) return emptyList()
        return text.substring(marker + "Changelog:".length)
            .trimStart()
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { LEADING_NUMBER.replace(it, "") }
            .filter { it.isNotEmpty() }
            .toList()
    }

    fun fromMessage(message: Message): AppUpdateInfo? {
        if (message.mediaKind != "document") return null
        val fileName = message.fileName ?: return null
        if (!fileName.endsWith(".apk", ignoreCase = true)) return null
        val key = message.mediaCacheKey ?: return null
        val text = message.text.orEmpty()
        val captionVersion = parseCaptionVersion(text) ?: return null
        return AppUpdateInfo(
            version = captionVersion.first,
            versionCode = captionVersion.second,
            description = text,
            changelog = parseChangelog(text),
            fileName = fileName,
            fileSize = message.fileSize ?: 0L,
            abi = "universal",
            buildType = "",
            chatId = message.id.chatId.value,
            messageId = message.id.id,
            mediaCacheKey = key,
        )
    }

    fun promptKey(info: AppUpdateInfo): String =
        "${info.versionCode}:${info.commit.orEmpty()}"

    /**
     * 从 GitHub Release 的 assets 里挑出最新的 Lunagram APK。
     *
     * @param assets  三元组 (下载直链, 文件名, 字节数)
     * @param buildType 当前安装的构建类型："release" 或 "beta"
     * @param betaAllowed 用户是否开启了测试版更新
     */
    fun latestReleaseAsset(
        assets: List<Triple<String, String, Long>>,
        buildType: String,
        betaAllowed: Boolean,
    ): Triple<String, String, Long>? {
        val wanted = compatibleBuildTypes(buildType)
        val parsed = assets.mapNotNull { asset ->
            val (version, isBeta) = parseLunagramApk(asset.second) ?: return@mapNotNull null
            val type = if (isBeta) "beta" else "release"
            if (type !in wanted) return@mapNotNull null
            // 正式版用户未开启 beta 时，不接受 beta 资产
            if (isBeta && !betaAllowed) return@mapNotNull null
            Triple(asset, version, versionToCode(version))
        }
        if (parsed.isEmpty()) return null
        val newestCode = parsed.maxOf { it.third }
        val newest = parsed.filter { it.third == newestCode }
        // 同版本号时，优先正式版（保证 beta -> release 能提示）
        return newest.firstOrNull { !it.first.second.contains("_beta", ignoreCase = true) }?.first
            ?: newest.first().first
    }

    /** 从 GitHub Release asset 构造更新信息 */
    fun fromReleaseAsset(
        downloadUrl: String,
        fileName: String,
        fileSize: Long,
        releaseBody: String,
        publishedAt: String? = null,
    ): AppUpdateInfo? {
        val (version, isBeta) = parseLunagramApk(fileName) ?: return null
        return AppUpdateInfo(
            version = version,
            versionCode = versionToCode(version),
            description = releaseBody,
            changelog = parseChangelog(releaseBody),
            fileName = fileName,
            fileSize = fileSize,
            abi = "universal",
            buildType = if (isBeta) "beta" else "release",
            chatId = 0L,
            messageId = 0,
            mediaCacheKey = "gh:$fileName",
            downloadUrl = downloadUrl,
            commit = publishedAt,
        )
    }

    fun select(
        infos: List<AppUpdateInfo>,
        supportedAbis: List<String>,
        buildType: String,
    ): AppUpdateInfo? {
        if (infos.isEmpty()) return null
        val wanted = compatibleBuildTypes(buildType)
        val typed = infos.filter {
            it.buildType.isEmpty() || it.buildType.lowercase() in wanted
        }.ifEmpty { infos }
        val newestCode = typed.maxOf { it.versionCode }
        val newest = typed.filter { it.versionCode == newestCode }
        // 同版本号优先正式版
        newest.find { it.buildType.equals("release", ignoreCase = true) }?.let { return it }
        return newest.firstOrNull()
    }

    fun afterCheck(
        authorized: Boolean,
        currentVersionCode: Int,
        selected: AppUpdateInfo?,
        currentCommit: String? = null,
        currentBuildType: String = "release",
    ): AppUpdateState {
        if (!authorized) return AppUpdateState.Idle
        if (selected == null) return AppUpdateState.Error(NO_UPDATE)
        if (selected.versionCode > currentVersionCode) return AppUpdateState.Available(selected)
        // 同版本号：测试版 -> 正式版 也算升级
        if (selected.versionCode == currentVersionCode) {
            val currentIsBeta = currentBuildType.equals("beta", ignoreCase = true)
            val selectedIsRelease = selected.buildType.equals("release", ignoreCase = true)
            if (currentIsBeta && selectedIsRelease) {
                return AppUpdateState.Available(selected)
            }
            if (selected.commit != null && !commitsMatch(selected.commit, currentCommit)) {
                return AppUpdateState.Available(selected)
            }
        }
        return AppUpdateState.UpToDate
    }

    const val INSTALL_FAILED = "install_failed"
    const val INSTALL_STATUS_SUCCESS = 0
    const val INSTALL_STATUS_PENDING_USER_ACTION = -1
    const val INSTALL_STATUS_FAILURE_ABORTED = 3

    enum class InstallStatusKind {
        Success,
        PendingUserAction,
        Cancelled,
        Failed,
    }

    fun installRequiresUserAction(selfInstaller: Boolean): Boolean = !selfInstaller

    fun installStatusKind(status: Int): InstallStatusKind = when (status) {
        INSTALL_STATUS_SUCCESS -> InstallStatusKind.Success
        INSTALL_STATUS_PENDING_USER_ACTION -> InstallStatusKind.PendingUserAction
        INSTALL_STATUS_FAILURE_ABORTED -> InstallStatusKind.Cancelled
        else -> InstallStatusKind.Failed
    }

    fun shouldOpenInstallConfirm(kind: InstallStatusKind): Boolean =
        kind == InstallStatusKind.PendingUserAction

    fun beginInstall(state: AppUpdateState): AppUpdateState {
        val ready = state as? AppUpdateState.ReadyToInstall ?: return state
        return AppUpdateState.Installing(ready.info, ready.filePath)
    }

    fun afterInstallStatus(
        installing: AppUpdateState.Installing,
        kind: InstallStatusKind,
        confirmShown: Boolean = true,
    ): AppUpdateState = when (kind) {
        InstallStatusKind.Success -> AppUpdateState.UpToDate
        InstallStatusKind.PendingUserAction ->
            if (confirmShown) {
                installing
            } else {
                AppUpdateState.Error(INSTALL_FAILED, installing.info)
            }
        InstallStatusKind.Cancelled ->
            AppUpdateState.ReadyToInstall(installing.info, installing.filePath)
        InstallStatusKind.Failed -> AppUpdateState.Error(INSTALL_FAILED, installing.info)
    }

    enum class InstallLaunch {
        Session,
        ExternalHandoff,
        Failed,
    }

    fun stateAfterInstallLaunch(
        installing: AppUpdateState.Installing,
        launch: InstallLaunch,
    ): AppUpdateState = when (launch) {
        InstallLaunch.Session,
        InstallLaunch.ExternalHandoff -> installing
        InstallLaunch.Failed -> AppUpdateState.Error(INSTALL_FAILED, installing.info)
    }

    fun afterExternalHandoffEnded(installing: AppUpdateState.Installing): AppUpdateState =
        AppUpdateState.ReadyToInstall(installing.info, installing.filePath)
}
