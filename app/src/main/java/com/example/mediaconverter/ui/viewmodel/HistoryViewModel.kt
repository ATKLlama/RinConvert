package com.example.mediaconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaconverter.data.HistoryItem
import com.example.mediaconverter.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    // UI state
    private val _historyItems = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyItems: StateFlow<List<HistoryItem>> = _historyItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mediaRepository.getHistoryFlow().collect { items ->
                    _historyItems.value = items
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load history: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            try {
                mediaRepository.deleteHistoryItem(id)
                // No need to call loadHistory() as getHistoryFlow() should be reactive
            } catch (e: Exception) {
                _error.value = "Failed to delete item: ${e.message}"
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                mediaRepository.clearHistory()
                // No need to call loadHistory()
            } catch (e: Exception) {
                _error.value = "Failed to clear history: ${e.message}"
            }
        }
    }

    fun retryDownload() {
        // TODO: Implement retry logic
    }

    fun shareItem() {
        // TODO: Implement sharing
    }
}