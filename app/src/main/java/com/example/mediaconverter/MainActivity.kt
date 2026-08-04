package com.example.mediaconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mediaconverter.ui.navigation.AppNavHost
import com.example.mediaconverter.ui.theme.MediaConverterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaConverterTheme {
                AppNavHost()
            }
        }
    }
}