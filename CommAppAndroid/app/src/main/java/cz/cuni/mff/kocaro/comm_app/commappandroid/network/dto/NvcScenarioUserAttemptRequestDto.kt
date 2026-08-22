package cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto

import com.google.gson.annotations.SerializedName

data class NvcScenarioUserAttemptRequestDto(
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("scenarioId") val scenarioId: Long,
    @SerializedName("selectedOptionIds") val selectedOptionIds: List<Long>
)
