package org.lunagram.feature.settings

import kotlinx.coroutines.flow.StateFlow
import org.lunagram.core.models.AppUpdateState

interface AppUpdateController {
    val state: StateFlow<AppUpdateState>
    fun checkForUpdates()
    fun downloadUpdate()
    fun cancelDownload()
    fun installUpdate()
}
