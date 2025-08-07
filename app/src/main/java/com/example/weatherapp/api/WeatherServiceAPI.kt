package com.example.weatherapp.api

import com.example.weatherapp.BuildConfig
import retrofit2.Call
import retrofit2.http.*

interface WeatherServiceAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }

    @GET("search.json?key=$API_KEY&lang=pt_br")
    fun search(@Query("q") query: String): Call<List<APILocation>?>

    @GET("current.json?key=$API_KEY&lang=pt")
    fun weather(@Query("q") query: String): Call<APICurrentWeather?>
}
