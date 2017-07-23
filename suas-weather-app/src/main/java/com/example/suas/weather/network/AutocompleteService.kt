package com.example.suas.weather.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface AutocompleteService {

    @GET
    fun autocomplete(
            @Url url: String = "http://autocomplete.wunderground.com/aq",
            @Query("query") query: String,
            @Query("h") hurricanes: Int = 0
    ): Call<NetworkModels.AutocompleteResult>

}