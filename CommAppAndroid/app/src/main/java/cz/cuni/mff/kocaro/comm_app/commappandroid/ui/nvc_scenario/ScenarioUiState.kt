package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

sealed interface ScenarioUiState {
    data object Loading : ScenarioUiState
    data class Error(val message: String) : ScenarioUiState

    data class Active(
        val scenario: NvcScenarioUiModel,
        val currentPhase: NvcPhase = NvcPhase.OBSERVATION,

        val selectedOptionIds: PersistentSet<Long> = persistentSetOf(),
        val evaluatedOptionIds: PersistentSet<Long> = persistentSetOf(),
        val isEvaluated: Boolean = false,
        val isSummaryCompleted: Boolean = false,

        val sessionSelectedOptionIds: PersistentSet<Long> = persistentSetOf()
    ) : ScenarioUiState {

        val remainingOptions: ImmutableList<NvcOptionUiModel> = scenario.options
            .filter { it.phase == currentPhase }
            .filterNot { evaluatedOptionIds.contains(it.id) }
            .toImmutableList()
    }
}