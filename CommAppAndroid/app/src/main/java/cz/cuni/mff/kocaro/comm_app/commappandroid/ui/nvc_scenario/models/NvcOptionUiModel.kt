package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase

data class NvcOptionUiModel(
    val id: Long,
    val phase: NvcPhase,
    val text: String,
    val isCorrect: Boolean,
    val feedback: String?
)