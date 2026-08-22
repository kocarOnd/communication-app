package cz.cuni.mff.kocaro.comm_app.commappandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiClient
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScenarioUiState {
    data object Loading : ScenarioUiState
    data class Success(val scenario: NvcScenarioResponseDto) : ScenarioUiState
    data class Error(val message: String) : ScenarioUiState
}

class NvcScenarioViewModel : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow<ScenarioUiState>(ScenarioUiState.Loading)
    // Public immutable state that Jetpack Compose will observe
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()

    init {
        // Fetch a scenario immediately when the ViewModel is first created
        fetchNewScenario()
    }

    fun fetchNewScenario() {
        _uiState.value = ScenarioUiState.Loading

        // viewModelScope ensures this coroutine is safely cancelled if the ViewModel is cleared
        viewModelScope.launch {
            try {
                val response = NvcScenarioApiClient.apiService.getRandomScenario()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ScenarioUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ScenarioUiState.Error("HTTP Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ScenarioUiState.Error(e.message ?: "Unknown network error")
            }
        }
    }
}