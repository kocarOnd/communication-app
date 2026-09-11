package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models

import kotlinx.collections.immutable.ImmutableList

data class NvcScenarioUiModel(
    val id: Long,
    val title: String,
    val contextDescription: String,
    val options: ImmutableList<NvcOptionUiModel> // Strict stability enforced here
)
