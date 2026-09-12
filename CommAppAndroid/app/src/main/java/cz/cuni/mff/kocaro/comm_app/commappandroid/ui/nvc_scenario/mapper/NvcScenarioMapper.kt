package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.mapper

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioOptionDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.toImmutableList

/**
 * Maps Scenario DTO to Scenario UI model
 *
 * @return [NvcScenarioUiModel]
 */
fun NvcScenarioResponseDto.toUiModel(): NvcScenarioUiModel {
    return NvcScenarioUiModel(
        id = this.id,
        title = this.title,
        contextDescription = this.contextDescription,
        options = this.options.map { it.toUiModel() }.toImmutableList()
    )
}

/**
 * Maps option DTO to an option UI model
 *
 * @return [NvcOptionUiModel]
 */
fun NvcScenarioOptionDto.toUiModel(): NvcOptionUiModel {
    return NvcOptionUiModel(
        id = this.id,
        phase = this.phase,
        text = this.text,
        isCorrect = this.isCorrect,
        feedback = this.feedback
    )
}