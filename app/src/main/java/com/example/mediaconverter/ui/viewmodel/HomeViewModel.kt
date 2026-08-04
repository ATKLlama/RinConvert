package com.example.mediaconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaconverter.domain.repository.MediaRepository
import com.example.mediaconverter.data.HistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val url: String = "",
    val isUrlValid: Boolean = false,
    val isConvertEnabled: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Navigation flag
    private val _navigateToConversion = MutableStateFlow(false)
    val navigateToConversion: StateFlow<Boolean> = _navigateToConversion.asStateFlow()

    val isConvertEnabled: StateFlow<Boolean> = uiState
        .map { it.url.isNotBlank() && it.isUrlValid }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onUrlChanged(url: String) {
        val urls = url.lines().map(String::trim).filter(String::isNotBlank)
        val isValid = urls.isNotEmpty() && urls.all {
            android.util.Patterns.WEB_URL.matcher(it).matches() || it.startsWith("content://")
        }
        _uiState.update { it.copy(url = url, isUrlValid = isValid) }
    }

    fun clearUrl() {
        _uiState.update { it.copy(url = "", isUrlValid = false) }
    }

    fun onConvertClicked() {
        // When convert is clicked, set navigation flag if URL is valid
        val currentState = _uiState.value
        if (currentState.url.isNotBlank() && currentState.isUrlValid) {
            _navigateToConversion.value = true
        }
    }

    fun onNavigated() {
        _navigateToConversion.value = false
    }
}
