package com.example.abschlussprojekt.data.remote

import com.example.abschlussprojekt.data.model.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//Base URL für die API
private const val BASE_URL = "https://api.open-meteo.com/v1/"

//Logger für HTTP-Anfragen und Antworten
private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

// HttpClient für Retrofit
private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

// Moshi für JSON Konvertierung
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// Retrofit für API Calls
private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

// Hier Interface für die API Endpunkte
interface WeatherApiService {
    @GET("forecast?&current_weather=true")
    suspend fun getCurrentWeather(@Query ("latitude") latitude: Double, @Query ("longitude") longitude: Double): WeatherResponse
}

// API Objekt
object WeatherApi {
    val retrofitService: WeatherApiService by lazy { retrofit.create(WeatherApiService::class.java) }
}

