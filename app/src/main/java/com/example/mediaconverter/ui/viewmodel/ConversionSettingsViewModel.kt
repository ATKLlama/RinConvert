package com.example.mediaconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConversionSettingsUiState(
    val outputFormat: String = "MP4",
    val videoQuality: String = "720p",
    val audioBitrate: String = "128 kbps",
    val trimEnabled: Boolean = false,
    val startTime: String = "00:00:00",
    val endTime: String = "00:00:00",
    val isFormValid: Boolean = false
)

@HiltViewModel
class ConversionSettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConversionSettingsUiState())
    val uiState: StateFlow<ConversionSettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ConversionSettingsUiState()
        // Update form validity whenever any field changes
        viewModelScope.launch {
            uiState.collect { state ->
                // Simple validation: non-empty fields
                val isValid = state.outputFormat.isNotBlank() &&
                        state.videoQuality.isNotBlank() &&
                        state.audioBitrate.isNotBlank() &&
                        (!state.trimEnabled || (state.startTime.isNotBlank() && state.endTime.isNotBlank()))
                _uiState.update { it.copy(isFormValid = isValid) }
            }
        }
    }

    fun onOutputFormatChanged(format: String) {
        _uiState.update { it.copy(outputFormat = format) }
    }

    fun onVideoQualityChanged(quality: String) {
        _uiState.update { it.copy(videoQuality = quality) }
    }

    fun onAudioBitrateChanged(bitrate: String) {
        _uiState.update { it.copy(audioBitrate = bitrate) }
    }

    fun onTrimEnabledChanged(enabled: Boolean) {
        _uiState.update { it.copy(trimEnabled = enabled) }
    }

    fun onStartTimeChanged(time: String) {
        _uiState.update { it.copy(startTime = time) }
    }

    fun onEndTimeChanged(time: String) {
        _uiState.update { it.copy(endTime = time) }
    }

    // For navigation to preview
    private val _navigateToPreview = MutableStateFlow(false)
    val navigateToPreview: StateFlow<Boolean> = _navigateToPreview.asStateFlow()

    fun onPreviewClicked() {
        val currentState = _uiState.value
        if (currentState.isFormValid) {
            _navigateToPreview.value = true
        }
    }

    fun onNavigatedToPreview() {
        _navigateToPreview.value = false
    }
}