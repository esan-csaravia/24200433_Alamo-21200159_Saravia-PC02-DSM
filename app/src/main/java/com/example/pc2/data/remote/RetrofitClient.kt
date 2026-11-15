package com.example.pc2.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val SCIENCE_BASE_URL = "https://science.nasa.gov/"
    private const val API_BASE_URL = "https://api.nasa.gov/"

    val scienceRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SCIENCE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
