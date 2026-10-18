package org.lunagram.feature.dialog.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.lunagram.core.common.Outcome
import org.lunagram.core.models.GeoPlace
import org.lunagram.core.models.Message
import org.lunagram.core.models.PeerId
import org.lunagram.core.models.UploadItem
import org.lunagram.core.models.displayedChatAction
import org.lunagram.core.models.peerAvatarCacheKey
import org.lunagram.core.ui.AppearanceSettings
import org.lunagram.core.ui.components.AppModalSheet
import org.lunagram.core.ui.components.AppStatusBanner
import org.lunagram.core.ui.components.AppSyncStatus
import org.lunagram.core.ui.components.ChatWallpaper
import org.lunagram.core.ui.components.MessageListSkeleton
import org.lunagram.core.ui.components.OnlineLeaseExpiry
import org.lunagram.core.ui.components.PeerAvatar
import org.lunagram.core.ui.components.SearchField
import org.lunagram.core.ui.components.TypingDots
import org.lunagram.core.ui.components.UnreadBadge
import org.lunagram.core.ui.components.listItemMotion
import org.lunagram.core.ui.components.liveActionTransition
import org.lunagram.core.ui.components.peerStatusLabel
import org.lunagram.core.ui.components.rememberPeerStatusNow
import org.lunagram.core.ui.components.typingStatusText
import org.lunagram.core.ui.components.uiLabel
import org.lunagram.core.ui.loading.MonogramLoading
import org.lunagram.core.ui.loading.MonogramLoadingListSize
import org.lunagram.core.ui.menu.AppMenuPlacementState
import org.lunagram.core.ui.menu.AppMenuScrimPopup
import org.lunagram.core.ui.rememberEnsuredFile
import org.lunagram.feature.dialog.ComposerAt
import org.lunagram.feature.dialog.ComposerPanels
import org.lunagram.feature.dialog.DialogComponent
import org.lunagram.feature.dialog.DialogDayKind
import org.lunagram.feature.dialog.DialogStore
import org.lunagram.feature.dialog.DialogTime
import org.lunagram.feature.dialog.R
import org.lunagram.feature.dialog.albumSlice
import org.lunagram.feature.dialog.dialogSyncStatus
import org.lunagram.feature.dialog.followBottomFromScroll
import org.lunagram.feature.dialog.historyPagingAllowed
import org.lunagram.feature.dialog.isAlbumHead
import org.lunagram.feature.dialog.liftAnchorTarget
import org.lunagram.feature.dialog.shouldFollowIncomingNewest
import org.lunagram.feature.dialog.shouldPageNewer
import org.lunagram.feature.dialog.shouldPageOlder
import org.lunagram.feature.dialog.unreadDividerIndex
import org.lunagram.network.http.MediaPriority
import java.io.File
import java.time.ZoneId
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import org.lunagram.core.ui.components.SponsorBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContent(component: DialogComponent, modifier: Modifier = Modifier) {
    val state by component.state.collectAsState()
    var mediaViewerSeenBy by remember { mutableStateOf<Message?>(null) }
    val receiptHolder = remember { ReadReceiptHolder() }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    CompositionLocalProvider(LocalMarkupParser provides component.markup, LocalReadReceiptHolder provides receiptHolder) {
        MessageMediaViewerScope(
            repository = component.mediaRepository,
            onCaptionUrl = { url -> absoluteLink(url)?.let { runCatching { uriHandler.openUri(it) } } },
            onCaptionMention = { target -> runCatching { uriHandler.openUri(target) } },
            chatCanForward = state.canForward,
            onForward = component::onForwardMessages,
            onDelete = { messages, revoke ->
                messages.forEach { component.onDelete(it.id.id, revoke) }
            },
            deleteOffer = { messages ->
                deleteOffer(state, messages, System.currentTimeMillis() / 1000)
            },
            onShowInChat = { message -> component.onJumpToMessage(message.id.id) },
            onReply = if (state.canSendPlain || state.canSendPhotos) {
                { message ->
                    if (messageMenuActions(state, message).canReply) {
                        component.onReply(message, focusComposer = true)
                    }
                }
            } else null,
            onEnsureReceipts = { message -> component.onLoadReadReceipts(message.id.id) },
            onOpenSeenBy = { message -> mediaViewerSeenBy = message },
            knownAlbumMessages = { message ->
                state.messages.filter {
                    it.id.chatId == message.id.chatId && it.groupedId == message.groupedId
                }
            },
        ) {
            DialogScreen(component, modifier)
        }
        val sheetMessage = mediaViewerSeenBy
        if (sheetMessage != null) {
            AppModalSheet(onDismissRequest = { mediaViewerSeenBy = null }) {
                MessageSeenByRow(
                    viewers = receiptHolder.viewers?.invoke(sheetMessage),
                    viewerAvatar = { viewer -> receiptHolder.avatar?.invoke(viewer) },
                    mediaRepository = component.mediaRepository,
                    onOpenProfile = { id ->
                        mediaViewerSeenBy = null
                        component.onOpenPeer(PeerId(id))
                    },
                    startExpanded = true,
                )
            }
        }
    }
}
