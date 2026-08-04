package com.example.mediaconverter

import android.app.Application
import android.util.Log
import com.example.mediaconverter.util.FFmpegUtils
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Delay FFmpeg initialization until conversion is actually needed.
        // This prevents startup crashes when native FFmpegKit loading fails on certain devices.
        // FFmpeg will still be initialized lazily when the worker runs.
        // FFmpegUtils.initialize()
    }
}