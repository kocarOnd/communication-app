package cz.cuni.mff.kocaro.comm_app.commappandroid.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NvcScenarioApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080"

    val apiService: NvcScenarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NvcScenarioApiService::class.java)
    }
}