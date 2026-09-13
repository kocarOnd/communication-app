package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiClient
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiService
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioUserAttemptRequestDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.security.getDeviceId
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.mapper.toUiModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.coroutines.cancellation.CancellationException

/**
 * An interface facilitating conversation between NavHost and [NvcScenarioViewModel]
 */
sealed interface NvcUiEvent {
    data object ReturnToMainMenu : NvcUiEvent
    data object AdvanceToMultiSelect : NvcUiEvent
    data object AdvanceToSwipePhase : NvcUiEvent
    data object AdvanceToSwipeSummary : NvcUiEvent
    data object AdvanceToFullReport : NvcUiEvent
}

/**
 * The ViewModel for NVC Scenario exercise context, responsible for keeping information out of UI
 */
class NvcScenarioViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionDeviceId: String = getDeviceId(application)
    private val _uiState = MutableStateFlow<ScenarioUiState>(ScenarioUiState.Loading)
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()
    private val _uiEvent = Channel<NvcUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var apiService: NvcScenarioApiService = NvcScenarioApiClient.apiService

    /**
     * A small, in-memory queue of previous user attempts that could not have been delivered to backend
     */
    private val failedAttemptQueue = mutableListOf<NvcScenarioUserAttemptRequestDto>()

    /**
     * Function responsible for fetching scenarios and starting the exercise
     */
    fun fetchNewScenario() {
        _uiState.value = ScenarioUiState.Loading

        // viewModelScope ensures this coroutine is safely cancelled if the ViewModel is cleared
        viewModelScope.launch {
            try {
                val response = apiService.getRandomScenario()
                if (response.isSuccessful && response.body() != null) {
                    val rawScenario = response.body()!!

                    val randomizedScenario = rawScenario.copy(
                        options = rawScenario.options.shuffled()
                    )

                    val stableUiModel = randomizedScenario.toUiModel()

                    _uiState.value = ScenarioUiState.Active(scenario = stableUiModel)

                    _uiEvent.send(NvcUiEvent.AdvanceToMultiSelect)
                } else {
                    _uiState.value = ScenarioUiState.Error("HTTP Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ScenarioUiState.Error(e.message ?: "Unknown network error")
            }
        }
    }

    /**
     * Function handling the changes to selection in multiselection the exercise
     */
    fun toggleOptionSelection(optionId: Long) {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        if (currentState.isEvaluated) return

        val newSelections = if (currentState.selectedOptionIds.contains(optionId)) {
            currentState.selectedOptionIds.removing(optionId)
            } else {
            currentState.selectedOptionIds.adding(optionId)
            }

        _uiState.value = currentState.copy(selectedOptionIds = newSelections)
    }

    /**
     * Flags the current state as evaluated
     */
    fun submitPhase() {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return
        _uiState.value = currentState.copy(isEvaluated = true)
    }

    /**
     * Records the choice upon swiping in the swiping exercise
     */
    fun recordSwipe(optionId: Long, isSelected: Boolean) {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        val newEvaluated = currentState.evaluatedOptionIds.adding(optionId)
        val newSelected = if (isSelected) {
            currentState.selectedOptionIds.adding(optionId)
        } else {
            currentState.selectedOptionIds
        }

        val updatedState = currentState.copy(
            evaluatedOptionIds = newEvaluated,
            selectedOptionIds = newSelected
        )

        _uiState.value = updatedState

        if (updatedState.remainingOptions.isEmpty()) {
            advanceToNextPhase()
        }
    }

    /**
     * submits the current selection to the backend (unless the summary is completed or the
     * selection is empty)
     */
    private fun submitUserAttempt(state: ScenarioUiState.Active) {
        val phaseSelections = state.selectedOptionIds.toList()
        val scenarioId = state.scenario.id

        if(phaseSelections.isEmpty() || state.isSummaryCompleted) return

        val currentDto = NvcScenarioUserAttemptRequestDto(
            deviceId = sessionDeviceId,
            scenarioId = scenarioId,
            selectedOptionIds = phaseSelections
        )

        viewModelScope.launch {
            val iterator = failedAttemptQueue.iterator()
            while (iterator.hasNext()) {
                val pastDto = iterator.next()
                try {
                    val response = apiService.submitAttempt(pastDto)
                    if (response.isSuccessful) {
                        iterator.remove()
                    } else {
                        break
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e

                    break
                }
            }

            try {
                val response = apiService.submitAttempt(currentDto)
                if (!response.isSuccessful) {
                    failedAttemptQueue.add(currentDto)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                failedAttemptQueue.add(currentDto)
            }
        }
    }

    /**
     * Function responsible for the linear progression of the NVC scenario exercise
     */
    fun advanceToNextPhase() {
        val currentState = _uiState.value as? ScenarioUiState.Active ?: return

        submitUserAttempt(state = currentState)

        val updatedSessionSelections =
            currentState.sessionSelectedOptionIds.addingAll(currentState.selectedOptionIds)

        val nextPhase = when (currentState.currentPhase) {
            NvcPhase.OBSERVATION -> NvcPhase.FEELING
            NvcPhase.FEELING -> NvcPhase.NEED
            NvcPhase.NEED -> NvcPhase.REQUEST
            NvcPhase.REQUEST -> null
        }

        if (nextPhase != null) {
            _uiState.value = currentState.copy(
                currentPhase = nextPhase,
                selectedOptionIds = persistentSetOf(),
                evaluatedOptionIds = persistentSetOf(),
                isEvaluated = false,
                sessionSelectedOptionIds = updatedSessionSelections
            )
            if (nextPhase == NvcPhase.REQUEST) {
                viewModelScope.launch { _uiEvent.send(NvcUiEvent.AdvanceToSwipePhase) }
            }
        } else {
            if (!currentState.isSummaryCompleted) {
                _uiState.value = currentState.copy(
                    isSummaryCompleted = true,
                    selectedOptionIds = currentState.selectedOptionIds,
                    evaluatedOptionIds = currentState.evaluatedOptionIds,
                    isEvaluated = false,
                    sessionSelectedOptionIds = updatedSessionSelections
                )
                viewModelScope.launch { _uiEvent.send(NvcUiEvent.AdvanceToSwipeSummary) }

            } else {
                viewModelScope.launch { _uiEvent.send(NvcUiEvent.AdvanceToFullReport) }
            }
        }
    }

    /**
     * Sends a navigation event to return to the menu
     */
    fun finishExerciseAndExit() {
        viewModelScope.launch {
            _uiEvent.send(NvcUiEvent.ReturnToMainMenu)
        }
    }
}