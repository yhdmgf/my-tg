package org.lunagram.feature.settings.ui

import android.graphics.Color
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.lunagram.core.common.CountryManager
import org.lunagram.core.models.AppUpdate
import org.lunagram.core.models.AppUpdateState
import org.lunagram.core.ui.AppUpdateSettings
import org.lunagram.core.ui.components.ItemPosition
import org.lunagram.core.ui.components.PeerAvatar
import org.lunagram.core.ui.components.SectionHeader
import org.lunagram.core.ui.components.SettingsTile
import org.lunagram.feature.settings.R
import org.lunagram.feature.settings.SettingsComponent
import org.lunagram.feature.settings.SettingsPage
import java.io.File


internal fun LazyListScope.homeItems(
    component: SettingsComponent,
    stateTitle: String,
    stateSubtitle: String,
    avatarFile: File?,
    avatarPreview: ByteArray? = null,
    appVersion: String,
    buildStamp: String,
    onOpen: (SettingsPage) -> Unit,
    onLogout: () -> Unit,
    loading: Boolean,
    sponsorIds: Set<Long>,
    selfPeerId: Long?,
    updateState: AppUpdateState = AppUpdateState.Idle,
    updatesEnabled: Boolean = true,
) {
    item {
        AccountHeader(
            title = stateTitle,
            subtitle = stateSubtitle,
            avatarFile = avatarFile,
            previewBytes = avatarPreview,
            onClick = component::onOpenProfile,
        )
    }
    item { SectionHeader(stringResource(R.string.settings_section_settings)) }
    item {
        SettingsTile(
            icon = Icons.Outlined.Palette,
            title = stringResource(R.string.settings_chat),
            subtitle = stringResource(R.string.settings_chat_sub),
            iconColor = MaterialTheme.colorScheme.primary,
            position = ItemPosition.TOP,
            onClick = { onOpen(SettingsPage.Appearance) },
            trailingContent = { Chevron() },
        )
    }
    item {
        SettingsTile(
            icon = Icons.Outlined.Folder,
            title = stringResource(R.string.settings_folders),
            subtitle = stringResource(R.string.settings_folders_sub),
            iconColor = MaterialTheme.colorScheme.tertiary,
            position = ItemPosition.MIDDLE,
            onClick = { onOpen(SettingsPage.Folders) },
            trailingContent = { Chevron() },
        )
    }
    item {
        SettingsTile(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.settings_notifications),
            subtitle = stringResource(R.string.settings_notifications_sub),
            iconColor = MaterialTheme.colorScheme.secondary,
            position = ItemPosition.MIDDLE,
            onClick = { onOpen(SettingsPage.Notifications) },
            trailingContent = { Chevron() },
        )
    }
    item {
        SettingsTile(
            icon = Icons.Outlined.VpnKey,
            title = stringResource(R.string.settings_proxy),
            subtitle = stringResource(R.string.settings_proxy_sub),
            iconColor = MaterialTheme.colorScheme.primary,
            position = ItemPosition.MIDDLE,
            onClick = { onOpen(SettingsPage.Proxy) },
            trailingContent = { Chevron() },
        )
    }
    item {
        SettingsTile(
            icon = Icons.Outlined.Storage,
            title = stringResource(R.string.settings_data),
            subtitle = stringResource(R.string.settings_data_sub),
            iconColor = MaterialTheme.colorScheme.tertiary,
            position = ItemPosition.BOTTOM,
            onClick = { onOpen(SettingsPage.Data) },
            trailingContent = { Chevron() },
        )
    }
    item { SectionHeader(stringResource(R.string.settings_section_about)) }
    if (updatesEnabled) {
        item {
            val betaPref by AppUpdateSettings.betaUpdates.collectAsState()
            // 所有人都能用 Beta 开关
            SettingsTile(
                icon = Icons.Outlined.Science,
                title = stringResource(R.string.settings_update_beta),
                subtitle = stringResource(R.string.settings_update_beta_sub),
                iconColor = MaterialTheme.colorScheme.tertiary,
                position = ItemPosition.TOP,
                enabled = true,
                onClick = {
                    AppUpdateSettings.setBetaUpdates(!betaPref)
                    component.onCheckForUpdates()
                },
                trailingContent = {
                    Switch(
                        checked = betaPref,
                        enabled = true,
                        onCheckedChange = { enabled ->
                            AppUpdateSettings.setBetaUpdates(enabled)
                            component.onCheckForUpdates()
                        },
                    )
                },
            )
        }
        item {
            val (title, subtitle) = updateTileCopy(updateState)
            val changelog = updateChangelog(updateState)
            Column(Modifier.fillMaxWidth()) {
                SettingsTile(
                    icon = Icons.Outlined.SystemUpdate,
                    title = title,
                    subtitle = subtitle,
                    iconColor = MaterialTheme.colorScheme.primary,
                    position = ItemPosition.BOTTOM,
                    enabled = updateState !is AppUpdateState.Checking,
                    onClick = {
                        when (updateState) {
                            is AppUpdateState.Available -> component.onDownloadUpdate()
                            is AppUpdateState.Downloading -> component.onCancelUpdateDownload()
                            is AppUpdateState.ReadyToInstall -> component.onInstallUpdate()
                            is AppUpdateState.Installing -> Unit
                            else -> component.onCheckForUpdates()
                        }
                    },
                )
                if (changelog.isNotEmpty()) {
                    Text(
                        text = changelog.joinToString("\n") { "• $it" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
    item { SectionHeader(stringResource(R.string.settings_section_session)) }
    item {
        SettingsTile(
            icon = Icons.AutoMirrored.Outlined.Logout,
            title = stringResource(R.string.settings_logout),
            iconColor = MaterialTheme.colorScheme.error,
            titleColor = MaterialTheme.colorScheme.error,
            position = ItemPosition.STANDALONE,
            enabled = !loading,
            onClick = onLogout,
        )
    }
    item {
        Text(
            text = "${stringResource(R.string.settings_about)} $appVersion\n$buildStamp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun updateTileCopy(state: AppUpdateState): Pair<String, String?> {
    return when (state) {
        AppUpdateState.Idle -> stringResource(R.string.settings_update_check) to
                stringResource(R.string.settings_update_check_sub)

        AppUpdateState.Checking -> stringResource(R.string.settings_update_checking) to null
        is AppUpdateState.Available -> {
            val sub = stringResource(
                R.string.settings_update_available_sub,
                state.info.fileName,
                formatUpdateBytes(state.info.fileSize),
            )
            val commit = state.info.commit?.takeIf { it.isNotBlank() }
            stringResource(
                R.string.settings_update_available,
                state.info.version,
                state.info.versionCode,
            ) to if (commit == null) sub else "$sub · $commit"
        }

        AppUpdateState.UpToDate -> {
            val currentVersion = try {
                BuildConfig.APP_VERSION_NAME
            } catch (e: Exception) {
                "unknown"
            }
            stringResource(R.string.settings_update_up_to_date) to
                stringResource(R.string.settings_update_up_to_date_sub) + " · $currentVersion"
        }

        is AppUpdateState.Downloading -> {
            val total = state.info.fileSize.takeIf { it > 0L } ?: 1L
            val percent = ((state.bytes * 100L) / total).toInt().coerceIn(0, 100)
            stringResource(R.string.settings_update_downloading) to stringResource(
                R.string.settings_update_downloading_sub,
                percent,
                formatUpdateBytes(state.info.fileSize),
            )
        }

        is AppUpdateState.ReadyToInstall -> stringResource(R.string.settings_update_ready) to
                stringResource(R.string.settings_update_ready_sub, state.info.version)

        is AppUpdateState.Installing -> stringResource(R.string.settings_update_installing) to
                stringResource(R.string.settings_update_installing_sub, state.info.version)

        is AppUpdateState.Error -> stringResource(R.string.settings_update_error) to
                if (state.message == AppUpdate.NO_UPDATE) {
                    stringResource(R.string.settings_update_none)
                } else {
                    stringResource(R.string.settings_update_error_sub)
                }
    }
}

private fun updateChangelog(state: AppUpdateState): List<String> {
    val info = when (state) {
        is AppUpdateState.Available -> state.info
        is AppUpdateState.Downloading -> state.info
        is AppUpdateState.ReadyToInstall -> state.info
        is AppUpdateState.Installing -> state.info
        else -> return emptyList()
    }
    return info.changelog.filterNot { line ->
        val commit = info.commit
        commit != null && line.equals(commit, ignoreCase = true)
    }
}

private fun formatUpdateBytes(bytes: Long): String {
    if (bytes <= 0L) return ""
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1.0) "${((mb * 10).toInt() / 10.0)} MB" else "${bytes / 1024} KB"
}

@Composable
private fun Chevron() {
    Icon(
        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun AccountHeader(
    title: String,
    subtitle: String,
    avatarFile: File?,
    previewBytes: ByteArray?,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            supportingContent = subtitle.takeIf { it.isNotBlank() }?.let {
                {
                    Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            leadingContent = {
                PeerAvatar(
                    title = title,
                    size = 64.dp,
                    imageFile = avatarFile,
                    previewBytes = previewBytes,
                )
            },
            trailingContent = { Chevron() },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Text(title, style = MaterialTheme.typography.titleLargeEmphasized)
        }
    }
}

// The signed-in account has no presence to show: Telegram reports its own status too.
@Composable
internal fun profileSubtitle(username: String?, phone: String?, loaded: Boolean): String {
    val handle = username?.takeIf { it.isNotBlank() }?.let { "@$it" }
    val number = phone?.takeIf { it.isNotBlank() }?.let(::displayPhone)
    return when {
        handle != null && number != null -> "$handle\n$number"
        handle != null -> handle
        number != null -> number
        loaded -> ""
        else -> stringResource(R.string.settings_account_loading)
    }
}

private fun displayPhone(raw: String): String {
    val trimmed = raw.trim()
    val international = runCatching { CountryManager.formatPhoneNumber(trimmed) }.getOrNull()
        ?.takeIf { it.isNotBlank() && it != trimmed }
    if (international != null) return international
    val withPlus = if (trimmed.startsWith("+")) trimmed else "+${trimmed.filter { it.isDigit() }}"
    return runCatching { CountryManager.formatPhoneNumber(withPlus) }.getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: withPlus
}
