package com.example.abschlussprojekt.data.model

data class Weather(
    val time : String,
    val interval: Int,
    val temperature : Double,
    val windspeed : Double,
    val winddirection : Double,
    val weathercode : Int,
    val is_day : Int,
)

data class WeatherResponse(
    val current_weather : Weather
)

