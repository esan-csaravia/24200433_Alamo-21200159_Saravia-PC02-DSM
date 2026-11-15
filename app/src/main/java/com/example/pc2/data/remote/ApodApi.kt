package com.example.pc2.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {
    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getApods(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<ApodDto>
}

object ApodApiClient {
    val api: ApodApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://science.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApodApi::class.java)
    }
}
