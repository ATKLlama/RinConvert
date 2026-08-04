package com.example.mediaconverter.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaconverter.ui.components.LucideIcons
import com.example.mediaconverter.ui.viewmodel.ConversionSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionSettingsScreen(navController: androidx.navigation.NavHostController, viewModel: ConversionSettingsViewModel = hiltViewModel(), mediaUrl: String, mediaType: String) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigate by viewModel.navigateToPreview.collectAsStateWithLifecycle()
    LaunchedEffect(navigate) { if (navigate) {
        val json = """{"outputFormat":"${state.outputFormat}","videoQuality":"${state.videoQuality}","audioBitrate":"${state.audioBitrate}","trimEnabled":${state.trimEnabled},"startTime":"${state.startTime}","endTime":"${state.endTime}"}"""
        navController.navigate("preview?mediaUri=${java.net.URLEncoder.encode(mediaUrl, "UTF-8")}&mediaType=$mediaType&conversionSettings=${java.net.URLEncoder.encode(json, "UTF-8")}")
        viewModel.onNavigatedToPreview()
    } }
    Scaffold(topBar = { TopAppBar(title = { Text("Conversion setup", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton({ navController.popBackStack() }) { Icon(LucideIcons.ArrowLeft, "Back") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (mediaType == "playlist") LucideIcons.ListVideo else LucideIcons.Link, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp)); Column { Text(if (mediaType == "playlist") "Playlist conversion" else "Single media conversion", fontWeight = FontWeight.SemiBold); Text("Choose the output that works for you", style = MaterialTheme.typography.bodySmall) }
                }
            }
            Text("Output options", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            SelectField("Format", state.outputFormat, listOf("MP4", "MKV", "WEBM", "MP3"), viewModel::onOutputFormatChanged)
            if (!state.outputFormat.equals("MP3", ignoreCase = true)) {
                SelectField("Video quality", state.videoQuality, listOf("360p", "480p", "720p", "1080p"), viewModel::onVideoQualityChanged)
            }
            SelectField("Audio quality", state.audioBitrate, listOf("64k", "128k", "192k", "320k"), viewModel::onAudioBitrateChanged)
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Trim clip", fontWeight = FontWeight.SemiBold); Text("Set a start and end time", style = MaterialTheme.typography.bodySmall) }; Switch(state.trimEnabled, viewModel::onTrimEnabledChanged) } }
            if (state.trimEnabled) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(state.startTime, viewModel::onStartTimeChanged, Modifier.weight(1f), label = { Text("Start") }, singleLine = true); OutlinedTextField(state.endTime, viewModel::onEndTimeChanged, Modifier.weight(1f), label = { Text("End") }, singleLine = true) }
            Spacer(Modifier.weight(1f))
            Button(onClick = viewModel::onPreviewClicked, enabled = state.isFormValid, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) { Text("Review conversion") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun SelectField(label: String, value: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        OutlinedTextField(value, {}, Modifier.menuAnchor().fillMaxWidth(), readOnly = true, label = { Text(label) }, trailingIcon = { Icon(LucideIcons.ChevronDown, null) })
        ExposedDropdownMenu(expanded, { expanded = false }) { options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false }, leadingIcon = { if (option == value) Icon(LucideIcons.Check, null) }) } }
    }
}
