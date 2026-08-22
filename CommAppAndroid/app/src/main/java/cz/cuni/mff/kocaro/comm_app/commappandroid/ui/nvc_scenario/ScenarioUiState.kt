package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto

sealed interface ScenarioUiState {
    data object Loading : ScenarioUiState
    data class Error(val message: String) : ScenarioUiState

    data class Active(
        val scenario: NvcScenarioResponseDto,
        val currentPhase: NvcPhase = NvcPhase.OBSERVATION,
        val selectedOptionIds: Set<Long> = emptySet(),

        val evaluatedOptionIds: Set<Long> = emptySet(),
        val isEvaluated: Boolean = false
    ) : ScenarioUiState {

        val remainingOptions = scenario.options
            .filter { it.phase == currentPhase }
            .filterNot { evaluatedOptionIds.contains(it.id) }
    }
}