package org.lunagram.update

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import org.lunagram.core.common.AppLog
import org.lunagram.core.common.Outcome
import org.lunagram.core.models.AppUpdate
import org.lunagram.core.models.AppUpdateInfo

@Serializable
data class GitHubRelease(
    val id: Long,
    val tag_name: String,
    val name: String,
    val body: String,
    val published_at: String?,
    val assets: List<GitHubReleaseAsset> = emptyList(),
)

@Serializable
data class GitHubReleaseAsset(
    val browser_download_url: String,
    val name: String,
    val size: Long = 0,
)

suspend fun fetchGitHubReleases(client: HttpClient): Outcome<List<GitHubRelease>> {
    return try {
        val response = client.get(AppUpdate.GITHUB_RELEASES) {
            header("Accept", "application/vnd.github+json")
            header("User-Agent", "Lunagram")
        }
        if (!response.status.isSuccess()) {
            AppLog.warn("update", "github releases http=${response.status.value}")
            Outcome.Err("HTTP ${response.status.value}")
        } else {
            val releases = response.body<List<GitHubRelease>>()
            AppLog.api(
                "update",
                "github releases listed=${releases.size}",
            )
            Outcome.Ok(releases)
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Outcome.Err(error.message ?: "github releases check failed", error)
    }
}

fun githubReleaseUpdateInfo(
    releases: List<GitHubRelease>,
    buildType: String,
    betaAllowed: Boolean,
): AppUpdateInfo? {
    val allAssets = releases.flatMap { release ->
        release.assets.filter { AppUpdate.isLunagramApk(it.name) }.map {
            Triple(it.browser_download_url, it.name, it.size)
        }
    }
    val picked = AppUpdate.latestReleaseAsset(allAssets, buildType, betaAllowed) ?: return null
    val latestRelease = releases.firstOrNull { rel ->
        rel.assets.any { it.browser_download_url == picked.first }
    }
    return AppUpdate.fromReleaseAsset(
        downloadUrl = picked.first,
        fileName = picked.second,
        fileSize = picked.third,
        releaseBody = latestRelease?.body ?: "",
        publishedAt = latestRelease?.published_at,
    )
}

suspend fun downloadHttpApk(
    client: HttpClient,
    url: String,
    destination: File,
    onProgress: (Long) -> Unit,
): Outcome<File> {
    val partial = File(destination.parentFile, ".${destination.name}.part")
    return try {
        destination.parentFile?.mkdirs()
        client.prepareGet(url) {
            header("User-Agent", "Lunagram")
        }.execute { response ->
            if (!response.status.isSuccess()) {
                error("HTTP ${response.status.value}")
            }
            val channel = response.bodyAsChannel()
            val buffer = ByteArray(64 * 1024)
            var total = 0L
            FileOutputStream(partial).use { output ->
                while (true) {
                    val read = channel.readAvailable(buffer)
                    if (read == -1) break
                    if (read == 0) continue
                    output.write(buffer, 0, read)
                    total += read
                    onProgress(total)
                }
            }
            if (!partial.renameTo(destination)) {
                error("cannot publish downloaded file")
            }
            Outcome.Ok(destination)
        }
    } catch (cancelled: CancellationException) {
        partial.delete()
        Outcome.Err("cancelled", cancelled)
    } catch (error: Exception) {
        partial.delete()
        Outcome.Err(error.message ?: "download failed", error)
    }
}

fun extractApkFromZip(zip: File, destDir: File, preferredNames: List<String>): File? {
    destDir.mkdirs()
    val available = linkedSetOf<String>()
    ZipInputStream(FileInputStream(zip)).use { input ->
        while (true) {
            val entry = input.nextEntry ?: break
            val name = File(entry.name).name
            if (name.endsWith(".apk", ignoreCase = true)) available += name
        }
    }
    val chosen = preferredNames.firstOrNull { wanted ->
        available.any { it.equals(wanted, ignoreCase = true) }
    } ?: available.firstOrNull() ?: return null
    ZipInputStream(FileInputStream(zip)).use { input ->
        while (true) {
            val entry = input.nextEntry ?: break
            val name = File(entry.name).name
            if (!name.equals(chosen, ignoreCase = true)) continue
            val out = File(destDir, name)
            FileOutputStream(out).use { output -> input.copyTo(output) }
            return out
        }
    }
    return null
}
