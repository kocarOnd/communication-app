package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiClient
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation.NvcScenarioExerciseRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface NvcUiEvent {
    data class Navigate(val route: String) : NvcUiEvent
}

class NvcScenarioViewModel : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow<ScenarioUiState>(ScenarioUiState.Loading)
    // Public immutable state that Jetpack Compose will observe
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()
    private val _uiEvent = Channel<NvcUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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
                    _uiState.value = ScenarioUiState.Active(scenario = response.body()!!)

                    // Trigger the navigation to leave the Loading screen
                    _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.MultiSelectPhase.route))
                } else {
                    _uiState.value = ScenarioUiState.Error("HTTP Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ScenarioUiState.Error(e.message ?: "Unknown network error")
            }
        }
    }

    fun toggleOptionSelection(optionId: Long) {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return
        // Lock selections once the user has pressed submit
        if (currentState.isEvaluated) return

        val newSelections = currentState.selectedOptionIds.toMutableSet()
        if (newSelections.contains(optionId)) {
            newSelections.remove(optionId)
        } else {
            newSelections.add(optionId)
        }

        _uiState.value = currentState.copy(selectedOptionIds = newSelections)
    }

    fun submitPhase() {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return
        _uiState.value = currentState.copy(isEvaluated = true)
    }

    fun advanceToNextPhase() {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        val nextPhase = when (currentState.currentPhase) {
            NvcPhase.OBSERVATION -> NvcPhase.FEELING
            NvcPhase.FEELING -> NvcPhase.NEED
            NvcPhase.NEED -> NvcPhase.REQUEST
            NvcPhase.REQUEST -> NvcPhase.SUMMARY
            NvcPhase.SUMMARY -> NvcPhase.SUMMARY
        }

        _uiState.value = currentState.copy(
            currentPhase = nextPhase,
            selectedOptionIds = emptySet(), // Clear previous choices
            isEvaluated = false // Reset evaluation lock
        )
        // Trigger one-time navigation events for structural screen changes
        viewModelScope.launch {
            when (nextPhase) {
                NvcPhase.REQUEST -> _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.SwipePhase.route))
                NvcPhase.SUMMARY -> _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.FeedbackSummary.route))
                else -> {} // MultiSelectPhase just re-renders in place; no navigation needed
            }
        }
    }
}