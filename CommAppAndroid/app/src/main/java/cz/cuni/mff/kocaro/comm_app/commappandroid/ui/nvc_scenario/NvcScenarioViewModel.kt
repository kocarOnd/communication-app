package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiClient
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation.GlobalRoute
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

    fun fetchNewScenario() {
        _uiState.value = ScenarioUiState.Loading

        // viewModelScope ensures this coroutine is safely cancelled if the ViewModel is cleared
        viewModelScope.launch {
            try {
                val response = NvcScenarioApiClient.apiService.getRandomScenario()
                if (response.isSuccessful && response.body() != null) {
                    val rawScenario = response.body()!!

                    val randomizedScenario = rawScenario.copy(
                        options = rawScenario.options.shuffled()
                    )

                    _uiState.value = ScenarioUiState.Active(scenario = randomizedScenario)

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

    fun recordSwipe(optionId: Long, isSelected: Boolean) {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        // Accumulate the swipe decisions
        val newEvaluated = currentState.evaluatedOptionIds + optionId
        val newSelected = if (isSelected) {
            currentState.selectedOptionIds + optionId
        } else {
            currentState.selectedOptionIds
        }

        // Mutate the state
        val updatedState = currentState.copy(
            evaluatedOptionIds = newEvaluated,
            selectedOptionIds = newSelected
        )

        _uiState.value = updatedState

        // Evaluate the threshold: If the deck is empty, force the final transition
        if (updatedState.remainingOptions.isEmpty()) {
            advanceToNextPhase()
        }
    }

    fun advanceToNextPhase() {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        val updatedSessionSelections = currentState.sessionSelectedOptionIds + currentState.selectedOptionIds

        val nextPhase = when (currentState.currentPhase) {
            NvcPhase.OBSERVATION -> NvcPhase.FEELING
            NvcPhase.FEELING -> NvcPhase.NEED
            NvcPhase.NEED -> NvcPhase.REQUEST
            NvcPhase.REQUEST -> NvcPhase.SWIPE_SUMMARY
            NvcPhase.SWIPE_SUMMARY -> NvcPhase.FULL_REPORT
            NvcPhase.FULL_REPORT -> NvcPhase.FULL_REPORT
        }

        val isTerminalPhase = nextPhase == NvcPhase.SWIPE_SUMMARY || nextPhase == NvcPhase.FULL_REPORT

        _uiState.value = currentState.copy(
            currentPhase = nextPhase,
            selectedOptionIds = if (isTerminalPhase) currentState.selectedOptionIds else emptySet(),
            evaluatedOptionIds = if (isTerminalPhase) currentState.evaluatedOptionIds else emptySet(),
            isEvaluated = false,

            sessionSelectedOptionIds = updatedSessionSelections
        )

        // Trigger one-time navigation events for structural screen changes
        viewModelScope.launch {
            when (nextPhase) {
                NvcPhase.REQUEST -> _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.SwipePhase.route))
                NvcPhase.SWIPE_SUMMARY -> _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.SwipeSummary.route))
                NvcPhase.FULL_REPORT -> _uiEvent.send(NvcUiEvent.Navigate(NvcScenarioExerciseRoute.FullReport.route))
                else -> {} // MultiSelectPhase just re-renders in place; no navigation needed
            }
        }
    }

    fun finishExerciseAndExit() {
        viewModelScope.launch {
            _uiEvent.send(NvcUiEvent.Navigate(GlobalRoute.MainMenu.route))
        }
    }
}