package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models

import kotlinx.collections.immutable.ImmutableList

/**
 * The immutable UI model to be used within UI states
 */
data class NvcScenarioUiModel(
    val id: Long,
    val title: String,
    val contextDescription: String,
    val options: ImmutableList<NvcOptionUiModel>
)
