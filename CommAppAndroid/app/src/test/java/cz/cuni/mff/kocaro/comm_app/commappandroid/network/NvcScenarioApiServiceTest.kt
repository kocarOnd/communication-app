package cz.cuni.mff.kocaro.comm_app.commappandroid.network

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NvcScenarioApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: NvcScenarioApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NvcScenarioApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getRandomScenario parses JSON successfully into DTOs`(): Unit = runBlocking {
        val mockJson = """
            {
                "id": 42,
                "title": "Roommate Dispute",
                "contextDescription": "Your roommate left the dishes again.",
                "options": [
                    {
                        "id": 101,
                        "phase": "OBSERVATION",
                        "text": "When I see the dishes in the sink...",
                        "isCorrect": true,
                        "feedback": "Good observation."
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
        )

        val response = apiService.getRandomScenario()

        assertTrue(response.isSuccessful)
        val body = response.body()

        assertEquals(42L, body?.id)
        assertEquals("Roommate Dispute", body?.title)
        assertEquals(1, body?.options?.size)
        assertEquals(NvcPhase.OBSERVATION, body?.options?.first()?.phase)
    }
}