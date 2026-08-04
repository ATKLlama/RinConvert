package com.example.mediaconverter.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.mediaconverter.data.HistoryItem
import com.example.mediaconverter.domain.repository.MediaRepository
import com.example.mediaconverter.worker.ConversionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class PreviewUiState(
    val isProcessing: Boolean = false,
    val progress: Int = 0,
    val errorMessage: String? = null,
    val mediaUrl: String = "",
    val outputFormat: String = "",
    val videoQuality: String = "",
    val audioBitrate: String = "",
    val trimEnabled: Boolean = false,
    val startTime: String? = null,
    val endTime: String? = null,
    val batchTotal: Int = 1,
    val batchCompleted: Int = 0
)

sealed class WorkStatus { object Idle : WorkStatus(); object Running : WorkStatus(); object Success : WorkStatus(); object Failure : WorkStatus() }

@HiltViewModel
class PreviewViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val workManager: WorkManager,
    private val mediaRepository: MediaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()
    private val _workProgress = MutableStateFlow(0)
    val workProgress: StateFlow<Int> = _workProgress.asStateFlow()
    private val _workStatus = MutableStateFlow<WorkStatus>(WorkStatus.Idle)
    private val _navigateToHistory = MutableStateFlow(false)
    val navigateToHistory: StateFlow<Boolean> = _navigateToHistory.asStateFlow()

    fun startConversion(mediaUri: String, outputFormat: String, videoQuality: String?, audioBitrate: String?, trimEnabled: Boolean, startTime: String?, endTime: String?) =
        startConversions(listOf(mediaUri), outputFormat, videoQuality, audioBitrate, trimEnabled, startTime, endTime)

    /** Enqueues each source independently, allowing WorkManager to run multiple conversions in parallel. */
    fun startConversions(mediaUris: List<String>, outputFormat: String, videoQuality: String?, audioBitrate: String?, trimEnabled: Boolean, startTime: String?, endTime: String?) {
        val sources = mediaUris.map { it.trim() }.filter { it.isNotBlank() }.distinct()
        if (sources.isEmpty()) return
        _uiState.value = PreviewUiState(true, mediaUrl = sources.first(), outputFormat = outputFormat, videoQuality = videoQuality.orEmpty(), audioBitrate = audioBitrate.orEmpty(), trimEnabled = trimEnabled, startTime = startTime, endTime = endTime, batchTotal = sources.size)
        _workStatus.value = WorkStatus.Running
        _workProgress.value = 0

        sources.forEachIndexed { index, source ->
            val request = OneTimeWorkRequestBuilder<ConversionWorker>().setInputData(workDataOf(
                "input_uri" to source,
                "output_path" to generateOutputFileName(outputFormat),
                "output_format" to outputFormat,
                "video_quality" to videoQuality,
                "audio_bitrate" to audioBitrate,
                "trim_enabled" to trimEnabled,
                "start_time" to startTime,
                "end_time" to endTime
            )).addTag("conversion_work").build()
            workManager.enqueue(request)
            observeBatchItem(request.id.toString(), source, outputFormat, index, sources.size, request.id)
        }
    }

    private fun observeBatchItem(label: String, source: String, format: String, index: Int, total: Int, id: UUID) = viewModelScope.launch {
        var terminalHandled = false
        workManager.getWorkInfoByIdFlow(id).collect { info ->
            if (info == null || terminalHandled) return@collect
            if (info.state == WorkInfo.State.RUNNING) {
                val itemProgress = info.progress.getInt("progress", 0)
                val completed = _uiState.value.batchCompleted
                _workProgress.value = maxOf(_workProgress.value, ((completed * 100) + itemProgress) / total)
                return@collect
            }
            if (info.state != WorkInfo.State.SUCCEEDED && info.state != WorkInfo.State.FAILED && info.state != WorkInfo.State.CANCELLED) return@collect
            terminalHandled = true
            val completed = _uiState.value.batchCompleted + 1
            val failure = when (info.state) {
                WorkInfo.State.FAILED -> "Item ${index + 1}: ${info.outputData.getString("error") ?: "Conversion failed"}"
                WorkInfo.State.CANCELLED -> "Item ${index + 1} was cancelled"
                else -> null
            }
            if (info.state == WorkInfo.State.SUCCEEDED) saveToHistory(source, info.outputData.getString("output_path").orEmpty(), format)
            _uiState.update { it.copy(batchCompleted = completed, isProcessing = completed < total, errorMessage = failure ?: it.errorMessage) }
            _workProgress.value = completed * 100 / total
            if (completed == total) {
                _workStatus.value = if (_uiState.value.errorMessage == null) WorkStatus.Success else WorkStatus.Failure
                if (_workStatus.value is WorkStatus.Success) _navigateToHistory.value = true
            }
        }
    }

    private fun generateOutputFileName(format: String) = File(applicationContext.cacheDir, "media_${UUID.randomUUID()}.${format.lowercase()}").absolutePath

    private fun saveToHistory(input: String, output: String, format: String) = viewModelScope.launch {
        try { mediaRepository.saveHistory(HistoryItem(inputUrl = input, outputFilePath = output, outputFormat = format)) }
        catch (error: Exception) { Timber.e(error, "Failed to save conversion history") }
    }

    fun cancelConversion() { workManager.cancelAllWorkByTag("conversion_work"); _uiState.update { it.copy(isProcessing = false) }; _workStatus.value = WorkStatus.Idle }
    fun onNavigatedFromPreview() { _navigateToHistory.value = false }
}
