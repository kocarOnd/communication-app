package cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto

import com.google.gson.annotations.SerializedName

data class NvcScenarioOptionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("phase") val phase: NvcPhase,
    @SerializedName("text") val text: String,
    @SerializedName("isCorrect") val isCorrect: Boolean,
    @SerializedName("feedback") val feedback: String?
)
