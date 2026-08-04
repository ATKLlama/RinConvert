package com.example.mediaconverter.worker

import android.content.Context
import android.content.ContentValues
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.arthenica.ffmpegkit.ReturnCode
import androidx.core.net.toUri
import com.example.mediaconverter.util.FFmpegUtils
import com.example.mediaconverter.util.NotificationHelper
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("SpellCheckingInspection")
class ConversionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // Batch jobs can run together, so each worker needs its own foreground notification.
    private val notificationId: Int get() = (id.hashCode() and Int.MAX_VALUE)

    override suspend fun doWork(): Result {
        val inputUri = inputData.getString("input_uri") ?: return Result.failure()
        val outputPath = inputData.getString("output_path") ?: return Result.failure()
        val outputFormat = inputData.getString("output_format") ?: "mp4"
        val videoQuality = inputData.getString("video_quality") ?: "720p"
        val audioBitrate = inputData.getString("audio_bitrate") ?: "128k"
        val trimEnabled = inputData.getBoolean("trim_enabled", false)
        val startTime = inputData.getString("start_time")
        val endTime = inputData.getString("end_time")

        NotificationHelper.createNotificationChannel(applicationContext)

        val notification = NotificationHelper.createProgressNotification(
            applicationContext,
            "Converting media",
            "Starting conversion..."
        ).build()

        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
        setForeground(foregroundInfo)

        return withContext(Dispatchers.IO) {
            try {
                val inputSource = prepareInputSource(inputUri, outputFormat)
                val tempInputFile = inputSource.second

                if (tempInputFile == null || !tempInputFile.exists() || tempInputFile.length() == 0L) {
                    val errorMsg = "The selected input file could not be prepared for conversion."
                    NotificationHelper.updateNotification(
                        applicationContext,
                        notificationId,
                        "Conversion Failed",
                        errorMsg,
                        0,
                        true
                    )
                    return@withContext Result.failure(workDataOf("error" to errorMsg))
                }

                // yt-dlp's bundled FFmpeg supports MP3 encoding and embedded
                // artwork; the compact FFmpeg Kit package does not ship a stable
                // MP3 encoder. Avoid an unnecessary second conversion pass.
                if (outputFormat.equals("mp3", ignoreCase = true) && isYouTubeUrl(inputUri)) {
                    val publishedUri = publishToMediaLibrary(tempInputFile, tempInputFile.absolutePath)
                    NotificationHelper.updateNotification(
                        applicationContext, notificationId, "Conversion Complete",
                        "Your MP3 has been saved to Music", 100
                    )
                    return@withContext Result.success(workDataOf("output_path" to publishedUri))
                }

                val command = buildFFmpegCommand(
                    inputSource.first,
                    outputPath,
                    outputFormat,
                    videoQuality,
                    audioBitrate,
                    trimEnabled,
                    startTime,
                    endTime
                )

                Timber.d("FFmpegCommand: $command")

                val progressJob = launch {
                    var progress = 0
                    while (isActive && progress < 100) {
                        delay(1000)
                        // Keep the fallback estimate deliberately conservative;
                        // conversion durations vary with resolution and device speed.
                        progress = (progress + 2).coerceAtMost(95)
                        setProgress(workDataOf("progress" to progress))
                        NotificationHelper.updateNotification(
                            applicationContext,
                            notificationId,
                            "Converting media",
                            "Progress: $progress%",
                            progress
                        )
                    }
                }

                val session = try {
                    FFmpegUtils.execute(command)
                } finally {
                    // FFmpeg may fail while loading a native library. Ensure the
                    // progress job does not keep updating the notification after
                    // the worker has already reported that failure.
                    progressJob.cancel()
                }

                if (ReturnCode.isSuccess(session.returnCode)) {
                    NotificationHelper.updateNotification(
                        applicationContext,
                        notificationId,
                        "Conversion Complete",
                        "Your media has been converted successfully",
                        100
                    )
                    val publishedUri = publishToMediaLibrary(File(outputPath), outputPath)
                    tempInputFile.delete()
                    Result.success(workDataOf("output_path" to publishedUri))
                } else {
                    val ffmpegOutput = session.allLogsAsString ?: ""
                    val errorMsg = "FFmpeg failed to convert the media. returnCode=${session.returnCode}"
                    Timber.e("FFmpeg failed with result %s. Logs: %s", session.returnCode, ffmpegOutput.takeLast(2500))
                    NotificationHelper.updateNotification(
                        applicationContext,
                        notificationId,
                        "Conversion Failed",
                        errorMsg,
                        0,
                        true
                    )
                    tempInputFile.delete()
                    Result.failure(workDataOf("error" to errorMsg))
                }
            } catch (t: Throwable) {
                val errorMsg = t.message ?: t.toString()
                Timber.e(t, "Unexpected error in conversion worker")
                NotificationHelper.updateNotification(
                    applicationContext,
                    notificationId,
                    "Conversion Failed",
                    errorMsg,
                    0,
                    true
                )
                Result.failure(workDataOf("error" to errorMsg))
            }
        }
    }

    private fun buildFFmpegCommand(
        inputPath: String,
        outputPath: String,
        outputFormat: String,
        videoQuality: String?,
        audioBitrate: String?,
        trimEnabled: Boolean,
        startTime: String?,
        endTime: String?
    ): String {
        val isAudioOnly = outputFormat.equals("mp3", ignoreCase = true)
        if (isAudioOnly) {
            val audioCommand = mutableListOf(
                "-y", "-i", inputPath,
                "-map", "0:a:0?",
                // The optional second video stream is the embedded cover image.
                "-map", "0:v:1?",
                "-map_metadata", "0",
                "-c:a", "libmp3lame",
                "-c:v", "mjpeg",
                "-disposition:v", "attached_pic",
                "-id3v2_version", "3"
            )
            val bitrate = audioBitrate?.replace(Regex("[^0-9]"), "")?.takeIf { it.isNotBlank() } ?: "128"
            audioCommand.addAll(listOf("-b:a", "${bitrate}k"))
            if (trimEnabled && !startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
                audioCommand.addAll(listOf("-ss", startTime, "-to", endTime))
            }
            audioCommand.add(outputPath)
            return audioCommand.joinToString(" ")
        }

        val command = mutableListOf(
            "-y",
            "-i", inputPath,
            // libx264 is not available in the compact 16 KB-compatible build.
            // MPEG-4 is built into FFmpeg and produces an MP4 on all supported
            // devices without loading optional native device libraries.
            "-map", "0:v:0",
            "-map", "0:a?",
            // A second video stream is yt-dlp's embedded thumbnail. Keep it as
            // attached artwork in the output rather than exporting a separate image.
            "-map", "0:v:1?",
            "-c:v:0", "mpeg4",
            "-c:v:1", "mjpeg",
            "-disposition:v:1", "attached_pic",
            "-c:a", "aac",
            "-q:v", "5",
            "-threads", "0",
            "-map_metadata", "0",
            "-movflags", "+faststart"
        )

        if (trimEnabled && !startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
            command.addAll(listOf("-ss", startTime, "-to", endTime))
        }

        if (videoQuality != null) {
            val resolution = when (videoQuality) {
                "360p" -> "640x360"
                "480p" -> "854x480"
                "720p" -> "1280x720"
                "1080p" -> "1920x1080"
                else -> "1280x720"
            }
            command.addAll(listOf("-vf", "scale=$resolution"))
        }

        if (audioBitrate != null) {
            val bitrate = audioBitrate.replace(Regex("[^0-9]"), "") + "k"
            command.addAll(listOf("-b:a", bitrate))
        }

        command.add(outputPath)

        return command.joinToString(" ")
    }

    private fun prepareInputSource(uriString: String, outputFormat: String): Pair<String, File?> {
        val uri = uriString.toUri()
        val scheme = uri.scheme?.lowercase()

        return when (scheme) {
            "http", "https" -> {
                val host = uri.host?.lowercase().orEmpty()
                val downloadResult = if (isYouTubeLikeHost(host)) {
                    downloadYoutubeUrlToTempFile(uriString, outputFormat.equals("mp3", ignoreCase = true))
                } else {
                    downloadUrlToTempFile(uriString)
                }

                if (downloadResult.isSuccess) {
                    val downloadedFile = downloadResult.getOrThrow()
                    downloadedFile.absolutePath to downloadedFile
                } else {
                    val error = downloadResult.exceptionOrNull()?.message ?: "Unable to download the remote media file"
                    throw IllegalArgumentException(error)
                }
            }
            else -> {
                val tempFile = copyLocalUriToTempFile(uri).getOrNull()
                    ?: throw IllegalArgumentException("Unable to access the selected file")
                tempFile.absolutePath to tempFile
            }
        }
    }

    private fun downloadYoutubeUrlToTempFile(urlString: String, audioOnly: Boolean): kotlin.Result<File> {
        val extension = if (audioOnly) "mp3" else "mp4"
        val tempFile = File(applicationContext.cacheDir, "youtube_input_" + System.currentTimeMillis() + ".$extension")
        return try {
            ensureYoutubeDownloaderInitialized()

            try {
                val updateStatus = YoutubeDL.getInstance().updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
                Timber.i("YouTube downloader update status: %s", updateStatus)
            } catch (t: Throwable) {
                Timber.w(t, "Stable YouTube downloader update failed; trying nightly channel")
                try {
                    val nightlyStatus = YoutubeDL.getInstance().updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.NIGHTLY)
                    Timber.i("YouTube downloader nightly update status: %s", nightlyStatus)
                } catch (nightlyError: Throwable) {
                    Timber.w(nightlyError, "Nightly YouTube downloader update failed; continuing with installed binary")
                }
            }

            val request = YoutubeDLRequest(urlString)
            if (audioOnly) {
                request.addOption("-x")
                request.addOption("--audio-format", "mp3")
                request.addOption("--audio-quality", "0")
            } else {
                // Prefer H.264/AAC. The compact FFmpeg package deliberately omits
                // optional AV1 codecs, which previously caused repeated decode errors.
                request.addOption("-f", "bestvideo[vcodec^=avc1][ext=mp4]+bestaudio[acodec^=mp4a]/best[ext=mp4][vcodec^=avc1]/best")
                request.addOption("--merge-output-format", "mp4")
            }
            request.addOption("--add-metadata")
            request.addOption("--embed-metadata")
            request.addOption("--embed-thumbnail")
            request.addOption("--no-playlist")
            request.addOption("--no-check-certificate")
            request.addOption("--no-warnings")
            request.addOption("-o", tempFile.absolutePath)

            Timber.d("Executing YoutubeDL request for URL %s", urlString)
            YoutubeDL.getInstance().execute(request, "media-converter-${System.currentTimeMillis()}", null)

            if (!tempFile.exists() || tempFile.length() == 0L) {
                tempFile.delete()
                return kotlin.Result.failure(Exception("YouTube download completed but produced no usable media file."))
            }

            kotlin.Result.success(tempFile)
        } catch (t: Throwable) {
            tempFile.delete()
            val causeMessage = t.cause?.message?.let { " cause=$it" } ?: ""
            kotlin.Result.failure(Exception("Failed to download YouTube media: ${t.message}$causeMessage"))
        }
    }

    private fun ensureYoutubeDownloaderInitialized() {
        try {
            YoutubeDL.getInstance().init(applicationContext)
            com.yausername.ffmpeg.FFmpeg.getInstance().init(applicationContext)
            com.yausername.aria2c.Aria2c.getInstance().init(applicationContext)
        } catch (t: Throwable) {
            Timber.e(t, "Failed to initialize YouTube downloader components")
            throw t
        }
    }

    private fun isYouTubeUrl(uriString: String): Boolean =
        uriString.toUri().host?.let(::isYouTubeLikeHost) == true

    private fun publishToMediaLibrary(source: File, outputPath: String): String {
        if (!source.exists() || source.length() == 0L) {
            throw IllegalStateException("Conversion produced no output file.")
        }

        val extension = source.extension.lowercase().ifBlank { "mp4" }
        val isAudio = extension in setOf("mp3", "m4a", "aac", "wav", "ogg", "flac")
        val mimeType = when (extension) {
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "webm" -> "video/webm"
            "mp3" -> "audio/mpeg"
            "m4a" -> "audio/mp4"
            "aac" -> "audio/aac"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            else -> if (isAudio) "audio/*" else "video/*"
        }
        val collection = if (isAudio) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val destination = if (isAudio) "Music/MediaConverter" else "DCIM/MediaConverter"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, source.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, destination)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(collection, values) ?: throw IllegalStateException("Unable to create a public media file.")
        try {
            resolver.openOutputStream(uri)?.use { output -> source.inputStream().use { it.copyTo(output) } }
                ?: throw IllegalStateException("Unable to write the public media file.")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.update(uri, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
            }
            source.delete()
            Timber.i("Saved converted media to %s (from %s)", uri, outputPath)
            return uri.toString()
        } catch (t: Throwable) {
            resolver.delete(uri, null, null)
            throw t
        }
    }

    private fun downloadUrlToTempFile(urlString: String): kotlin.Result<File> {
        val tempFile = File(applicationContext.cacheDir, "temp_input_" + System.currentTimeMillis())
        return try {
            val url = URL(urlString)
            val host = url.host?.lowercase() ?: ""

            if (isYouTubeLikeHost(host)) {
                return kotlin.Result.failure(Exception("YouTube links are handled through the dedicated downloader path."))
            }

            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.instanceFollowRedirects = true
            connection.requestMethod = "GET"
            // Set a user-agent to mimic a browser
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                connection.disconnect()
                return kotlin.Result.failure(Exception("Failed to download file: HTTP $responseCode"))
            }

            // Check Content-Type header
            val contentType = connection.contentType?.lowercase() ?: ""
            val isMediaContent = isMediaContentType(contentType) || isMediaFileExtension(urlString)

            if (!isMediaContent) {
                connection.disconnect()
                tempFile.delete()
                return kotlin.Result.failure(Exception("URL does not point to a media file. Please use a direct link to an audio/video file or select a local file."))
            }

            connection.inputStream?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw Exception("No input stream from connection")
            connection.disconnect()

            // Additional validation: check file signature (magic bytes)
            // Some valid media files use less-common headers or are still readable by FFmpeg.
            // We only do a lightweight check and allow the download to continue if it is non-empty.
            if (!tempFile.exists() || tempFile.length() == 0L) {
                tempFile.delete()
                return kotlin.Result.failure(Exception("Downloaded file is empty. Please check the URL and try again."))
            }

            kotlin.Result.success(tempFile)
        } catch (e: Exception) {
            tempFile.delete()
            kotlin.Result.failure(Exception("Failed to download file: ${e.message}"))
        }
    }

    private fun copyLocalUriToTempFile(uri: Uri): kotlin.Result<File> {
        val context = applicationContext
        val tempFile = File(context.cacheDir, "temp_input_" + System.currentTimeMillis())
        return try {
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw Exception("Could not open input stream for URI: $uri")
            kotlin.Result.success(tempFile)
        } catch (e: Exception) {
            tempFile.delete()
            kotlin.Result.failure(Exception("Failed to access the selected file: ${e.message}"))
        }
    }

    @Deprecated("Use prepareInputSource instead")
    private fun copyUriToTempFile(uriString: String): File? {
        val uri = Uri.parse(uriString)
        val scheme = uri.scheme?.lowercase()
        return when (scheme) {
            "http", "https" -> downloadUrlToTempFile(uriString).getOrNull()
            else -> copyLocalUriToTempFile(uri).getOrNull()
        }
    }
}

private fun isMediaContentType(contentType: String): Boolean {
    return contentType.startsWith("video/") || contentType.startsWith("audio/")
}

private fun isMediaFileExtension(urlString: String): Boolean {
    val lowerUrl = urlString.lowercase()
    return listOf(
        ".mp4", ".mkv", ".avi", ".mov", ".wmv", ".flv", ".webm", ".m4v",  // Video
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a", ".wma"          // Audio
    ).any { lowerUrl.endsWith(it) }
}

private fun isYouTubeLikeHost(host: String): Boolean {
    return host.contains("youtube.com") || host.contains("youtu.be")
}

private fun isLikelyMediaFile(file: File): Boolean {
    return file.exists() && file.length() > 0L
}
