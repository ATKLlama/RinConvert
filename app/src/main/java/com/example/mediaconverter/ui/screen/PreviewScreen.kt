package com.example.mediaconverter.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaconverter.ui.components.LucideIcons
import com.example.mediaconverter.ui.viewmodel.PreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(navController: androidx.navigation.NavHostController, viewModel: PreviewViewModel = hiltViewModel(), mediaUrl: String, mediaType: String, conversionSettingsJson: String) {
    val state by viewModel.uiState.collectAsStateWithLifecycle(); val progress by viewModel.workProgress.collectAsStateWithLifecycle(); val toHistory by viewModel.navigateToHistory.collectAsStateWithLifecycle()
    val format = remember(conversionSettingsJson) { setting(conversionSettingsJson, "outputFormat", "MP4") }
    val quality = remember(conversionSettingsJson) { setting(conversionSettingsJson, "videoQuality", "720p") }
    val audio = remember(conversionSettingsJson) { setting(conversionSettingsJson, "audioBitrate", "128k") }
    val sources = remember(mediaUrl) { mediaUrl.lines().map { it.trim() }.filter { it.isNotBlank() } }
    LaunchedEffect(toHistory) { if (toHistory) { navController.navigate("history") { popUpTo("home") }; viewModel.onNavigatedFromPreview() } }
    Scaffold(topBar = { TopAppBar(title = { Text("Ready to convert", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton({ navController.popBackStack() }) { Icon(LucideIcons.ArrowLeft, "Back") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(22.dp)) { Icon(if (sources.size > 1 || mediaType == "playlist") LucideIcons.ListVideo else LucideIcons.Play, null, Modifier.size(32.dp), MaterialTheme.colorScheme.primary); Spacer(Modifier.height(14.dp)); Text(if (sources.size > 1) "${sources.size} files ready" else if (mediaType == "playlist") "Playlist is ready" else "Your video is ready", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(if (sources.size > 1) "Each file will convert independently." else mediaUrl, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer) } }
            Text("Conversion summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { AssistChip({}, label = { Text(format) }); AssistChip({}, label = { Text(quality) }); AssistChip({}, label = { Text(audio) }) }
            if (state.isProcessing) { LinearProgressIndicator({ progress / 100f }, Modifier.fillMaxWidth()); Text(if (sources.size > 1) "Converting ${state.batchCompleted} of ${state.batchTotal} · $progress%" else "Converting… $progress%", style = MaterialTheme.typography.labelLarge) }
            state.errorMessage?.let { Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(14.dp)) { Text(it, Modifier.padding(14.dp), color = MaterialTheme.colorScheme.onErrorContainer) } }
            Spacer(Modifier.weight(1f))
            Button(onClick = { if (state.isProcessing) viewModel.cancelConversion() else viewModel.startConversions(sources, format.lowercase(), quality, audio, false, null, null) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) { Icon(if (state.isProcessing) LucideIcons.ArrowLeft else LucideIcons.Play, null); Spacer(Modifier.width(10.dp)); Text(if (state.isProcessing) "Cancel conversion" else if (sources.size > 1) "Convert ${sources.size} files" else "Start conversion") }
            Text("Mady by atkllama", Modifier.fillMaxWidth().padding(bottom = 10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun setting(json: String, key: String, fallback: String): String = Regex("\\\"$key\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").find(json)?.groupValues?.get(1) ?: fallback
