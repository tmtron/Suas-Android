package com.example.suas.weather.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WundergroundService {

    @GET("/api/{key}/conditions/q/zmw:{id}.json")
    fun getConditions(
            @Path("key") key: String ="49415a822ff30632",
            @Path("id") id: String
    ): Call<NetworkModels.ConditionResponse>

}