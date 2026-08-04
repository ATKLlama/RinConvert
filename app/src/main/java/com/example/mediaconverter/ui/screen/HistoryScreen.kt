package com.example.mediaconverter.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaconverter.data.HistoryItem
import com.example.mediaconverter.ui.components.LucideIcons
import com.example.mediaconverter.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: androidx.navigation.NavHostController, viewModel: HistoryViewModel = hiltViewModel()) {
    val items by viewModel.historyItems.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Column { Text("Library", fontWeight = FontWeight.Bold); Text("Your completed conversions", style = MaterialTheme.typography.labelSmall) } }, navigationIcon = { IconButton({ navController.popBackStack() }) { Icon(LucideIcons.ArrowLeft, "Back") } }) }) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator() }
            error != null -> Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) { Text(error!!, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error) }
            items.isEmpty() -> EmptyLibrary(Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { Spacer(Modifier.height(4.dp)); Text("${items.size} completed ${if (items.size == 1) "conversion" else "conversions"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(items, key = { it.id }) { item -> HistoryCard(item, { viewModel.deleteHistoryItem(item.id) }) }
            }
        }
    }
}

@Composable private fun EmptyLibrary(modifier: Modifier) = Box(modifier, Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.primaryContainer) { Icon(LucideIcons.History, null, Modifier.padding(20.dp).size(42.dp), MaterialTheme.colorScheme.primary) }
        Spacer(Modifier.height(20.dp)); Text("Your library is ready", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp)); Text("Finished videos and audio files will appear here.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable private fun HistoryCard(item: HistoryItem, onDelete: () -> Unit) {
    val date = SimpleDateFormat("MMM d · h:mm a", Locale.getDefault()).format(item.createdAt)
    Surface(shape = RoundedCornerShape(20.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.secondaryContainer) { Icon(LucideIcons.Play, null, Modifier.padding(12.dp).size(22.dp), MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) {
                Text(item.inputUrl, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp)); Text("${item.outputFormat.uppercase()}  •  $date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(LucideIcons.Trash, "Remove from history", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
