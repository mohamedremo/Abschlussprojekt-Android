package com.example.abschlussprojekt.data.remote
import com.example.abschlussprojekt.data.model.Profiles
import com.example.abschlussprojekt.data.model.Task

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Hier die Konstante für die Base URL
private const val BASE_URL = "https://66d1786862816af9a4f3c20b.mockapi.io/mybutler/v1/"

// Hier die Moshi Instanz erstellen
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Hier das Retrofit Objekt erstellen
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

// Hier das Interface für die API Calls erstellen
interface MyButlerApiService {

    @GET("profiles")
    suspend fun getProfiles(): Profiles

    @GET("profiles/{id}")
    suspend fun getProfileById(@Path("id") id: Int): Profiles

    @GET("task")
    suspend fun getTasks(): List<Task>
}

// Hier das API Objekt erstellen
object ButlerApi {
    val retrofitService: MyButlerApiService by lazy { retrofit.create(MyButlerApiService::class.java) }
}
