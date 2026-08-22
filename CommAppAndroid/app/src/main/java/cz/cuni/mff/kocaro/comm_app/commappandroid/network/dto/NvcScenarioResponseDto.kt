package cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto

import com.google.gson.annotations.SerializedName

data class NvcScenarioResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("contextDescription") val contextDescription: String,
    @SerializedName("options") val options: List<NvcScenarioOptionDto>
)
