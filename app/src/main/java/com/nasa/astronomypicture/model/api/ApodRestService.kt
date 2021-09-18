package com.nasa.astronomypicture.model.api

import com.nasa.astronomypicture.model.ApodDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApodRestService {

    @GET("apod?")
    suspend fun getApodData(@Query("api_key")api_key: String, @Query(value = "date") date: String): Response<ApodDataModel>

}