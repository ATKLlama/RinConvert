package com.example.mediaconverter.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaconverter.ui.components.LucideIcons
import com.example.mediaconverter.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: androidx.navigation.NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val canConvert by viewModel.isConvertEnabled.collectAsStateWithLifecycle()
    val navigate by viewModel.navigateToConversion.collectAsStateWithLifecycle()
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) viewModel.onUrlChanged(uris.joinToString("\n") { it.toString() })
    }
    var playlistMode by rememberSaveable { mutableStateOf(false) }
    val queuedUrls = state.url.lines().map(String::trim).filter(String::isNotBlank)
    val isPlaylist = queuedUrls.any { it.contains("list=", ignoreCase = true) || it.contains("playlist", ignoreCase = true) }
    val isBatch = queuedUrls.size > 1

    LaunchedEffect(navigate) { if (navigate) {
        val url = java.net.URLEncoder.encode(state.url, "UTF-8")
        val type = when { isBatch -> "batch"; playlistMode -> "playlist"; else -> "single" }
        navController.navigate("conversion_settings?mediaUri=$url&mediaType=$type")
        viewModel.onNavigated()
    } }

    Scaffold(containerColor = MaterialTheme.colorScheme.surface) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(LucideIcons.Sparkles, null, Modifier.padding(12.dp).size(26.dp), MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("RinConvert", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Your private media workspace", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { navController.navigate("history") }) { Icon(LucideIcons.History, "Conversion history") }
            }

            Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer))).padding(22.dp)) {
                    Text("Convert without compromise", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Paste a link, choose an output, and keep every file where you can find it.", color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            OutlinedTextField(
                value = state.url, onValueChange = viewModel::onUrlChanged, modifier = Modifier.fillMaxWidth(), singleLine = false, minLines = 3, maxLines = 5,
                leadingIcon = { Icon(LucideIcons.Link, null) }, label = { Text("Media link") },
                placeholder = { Text("https://youtu.be/…\nPaste one link per line for a batch") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                isError = state.url.isNotBlank() && !state.isUrlValid,
                supportingText = { if (state.url.isNotBlank() && !state.isUrlValid) Text("Enter a valid web address") else Text("Metadata, cover art, and your files stay private") }
            )

            OutlinedButton(
                onClick = { filePicker.launch(arrayOf("video/*", "audio/*")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(LucideIcons.Folder, null)
                Spacer(Modifier.width(8.dp))
                Text("Choose multiple files")
            }

            if (isBatch) {
                AssistChip(onClick = {}, label = { Text("${queuedUrls.size} files queued for batch conversion") }, leadingIcon = { Icon(LucideIcons.ListVideo, null) })
            }

            if (isPlaylist) {
                Surface(shape = RoundedCornerShape(18.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(LucideIcons.ListVideo, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("Playlist detected", fontWeight = FontWeight.SemiBold); Text("Choose how this link is handled", style = MaterialTheme.typography.bodySmall) }
                        Switch(checked = playlistMode, onCheckedChange = { playlistMode = it })
                    }
                }
                Text(if (playlistMode) "Playlist mode selected" else "Single video mode selected", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = viewModel::onConvertClicked, enabled = canConvert, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
                Icon(if (playlistMode || isBatch) LucideIcons.ListVideo else LucideIcons.Play, null); Spacer(Modifier.width(10.dp)); Text(if (isBatch) "Set up ${queuedUrls.size}-file batch" else if (playlistMode) "Set up playlist" else "Set up conversion")
            }
            Text("Mady by atkllama", modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
