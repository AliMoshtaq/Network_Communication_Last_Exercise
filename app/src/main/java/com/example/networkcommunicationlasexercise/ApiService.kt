package com.example.networkcommunicationlasexercise

import com.example.networkcommunicationlasexercise.models.CovidData
import retrofit2.http.GET

interface ApiService {

    @GET("/statistics?country=italy")
    suspend fun getCovidDetails(): CovidData
}