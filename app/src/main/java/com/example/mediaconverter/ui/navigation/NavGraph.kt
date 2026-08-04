package com.example.mediaconverter.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mediaconverter.ui.screen.ConversionSettingsScreen
import com.example.mediaconverter.ui.screen.HistoryScreen
import com.example.mediaconverter.ui.screen.PreviewScreen
import com.example.mediaconverter.ui.screen.SettingsScreen
import com.example.mediaconverter.ui.screen.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("history") {
            HistoryScreen(navController = navController)
        }
        composable(
            "conversion_settings?mediaUri={mediaUri}&mediaType={mediaType}",
            arguments = listOf(
                navArgument("mediaUri") { type = NavType.StringType; defaultValue = "" },
                navArgument("mediaType") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("mediaUri") ?: ""
            val type = backStackEntry.arguments?.getString("mediaType") ?: ""
            ConversionSettingsScreen(
                navController = navController,
                mediaUrl = url,
                mediaType = type
            )
        }
        composable(
            "preview?mediaUri={mediaUri}&mediaType={mediaType}&conversionSettings={conversionSettings}",
            arguments = listOf(
                navArgument("mediaUri") { type = NavType.StringType; defaultValue = "" },
                navArgument("mediaType") { type = NavType.StringType; defaultValue = "" },
                navArgument("conversionSettings") { type = NavType.StringType; defaultValue = "{}" }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("mediaUri") ?: ""
            val type = backStackEntry.arguments?.getString("mediaType") ?: ""
            val settingsJson = backStackEntry.arguments?.getString("conversionSettings") ?: "{}"
            PreviewScreen(
                navController = navController,
                mediaUrl = url,
                mediaType = type,
                conversionSettingsJson = settingsJson
            )
        }
    }
}