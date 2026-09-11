package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.mapper

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioOptionDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.toImmutableList

// This function intercepts the unstable DTO and outputs a perfectly stable UI Model
fun NvcScenarioResponseDto.toUiModel(): NvcScenarioUiModel {
    return NvcScenarioUiModel(
        id = this.id,
        title = this.title,
        contextDescription = this.contextDescription,
        // This calls the second extension function on every item before locking the list
        options = this.options.map { it.toUiModel() }.toImmutableList()
    )
}

fun NvcScenarioOptionDto.toUiModel(): NvcOptionUiModel {
    return NvcOptionUiModel(
        id = this.id,
        phase = this.phase,
        text = this.text,
        isCorrect = this.isCorrect,
        feedback = this.feedback
    )
}