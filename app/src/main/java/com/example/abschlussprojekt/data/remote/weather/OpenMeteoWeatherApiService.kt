package com.example.abschlussprojekt.data.remote.weather

import com.example.abschlussprojekt.data.model.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.open-meteo.com/v1/"

private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

interface WeatherApiService {
    @GET("forecast?latitude=52.52&longitude=13.41&current_weather=true")
    suspend fun getCurrentWeather(): WeatherResponse
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy { retrofit.create(WeatherApiService::class.java) }
}



