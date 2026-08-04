package com.example.mediaconverter.util

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback

@Suppress("SpellCheckingInspection")
class FFmpegUtils private constructor() {
    companion object {
        private const val TAG = "FFmpegUtils"
        private var initialized = false

        fun initialize(): Boolean {
            if (initialized) return true
            return try {
                FFmpegKitConfig.enableLogCallback { log ->
                    Log.d(TAG, log.message)
                }
                FFmpegKitConfig.enableStatisticsCallback { stats ->
                    Log.d(TAG, "Progress: ${stats.videoFrameNumber}")
                }
                initialized = true
                Log.d(TAG, "FFmpeg initialized")
                true
            } catch (t: Throwable) {
                Log.e(TAG, "Error initializing FFmpeg", t)
                false
            }
        }

        /**
         * Run FFmpeg command asynchronously with progress callback.
         *
         * @param command   The FFmpeg command as a list of strings
         * @param callback  Callback to receive progress and completion events
         */
        fun executeAsync(
            command: String,
            callback: FFmpegSessionCompleteCallback
        ) {
            if (!initialize()) {
                throw IllegalStateException("FFmpegKit failed to initialize")
            }
            FFmpegKit.executeAsync(command, callback)
        }

        /**
         * Execute FFmpeg command synchronously.
         *
         * @param command The FFmpeg command as a string
         * @return The FFmpeg session
         */
        fun execute(command: String): FFmpegSession {
            if (!initialize()) {
                throw IllegalStateException("FFmpegKit failed to initialize")
            }
            return FFmpegKit.execute(command)
        }

        /**
         * Cancel the current FFmpeg execution.
         */
        fun cancel() {
            FFmpegKit.cancel()
        }

        /**
         * Get the last session's output.
         */
        fun getLastCommandOutput(): String {
            val sessions = FFmpegKitConfig.getSessions()
            return if (sessions.isNotEmpty()) {
                sessions.last().allLogsAsString
            } else {
                ""
            }
        }

        /**
         * Get the last return code.
         */
        fun getLastReturnCode(): Int {
            val sessions = FFmpegKitConfig.getSessions()
            return if (sessions.isNotEmpty()) {
                sessions.last().returnCode.value
            } else {
                -1
            }
        }
    }
}