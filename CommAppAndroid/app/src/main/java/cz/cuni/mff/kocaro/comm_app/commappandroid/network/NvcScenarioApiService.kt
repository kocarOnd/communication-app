package cz.cuni.mff.kocaro.comm_app.commappandroid.network

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioUserAttemptRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NvcScenarioApiService {

    @GET("/api/nvc/scenarios/random")
    suspend fun getRandomScenario(): Response<NvcScenarioResponseDto>

    @POST("/api/nvc/scenarios/attempt")
    suspend fun submitAttempt(@Body attempt: NvcScenarioUserAttemptRequestDto): Response<Void>
}