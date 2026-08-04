package com.example.mediaconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val autoDeleteAfterDays: Int = 30,
    val isFormValid: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val isFormValid: StateFlow<Boolean> = _uiState
        .map { it.isFormValid }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        // Load settings from SharedPreferences or DataStore
        // For now, we'll use default values
        _uiState.value = SettingsUiState()
    }

    fun setDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkModeEnabled = enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setAutoDeleteAfterDays(days: Int) {
        _uiState.update { it.copy(autoDeleteAfterDays = days) }
    }

    fun saveSettings() {
        // Save to SharedPreferences or DataStore
        // For now, just show a toast (we'll need a context)
        // In a real app, we would use a repository to save settings
        // and then show a snackbar or toast.
        // We'll leave this as a placeholder.
    }
}