package com.nasa.astronomypicture.model.api

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {

        val API_KEY = "kH0OQm8bmOBiWMWWUF6UbLmJYmwof6LiKG6bEDkf"
        val BASE_URL: String = "https://api.nasa.gov/planetary/"

        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        fun getClient(cacheFile : Cache) : OkHttpClient{
            return OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .cache(cacheFile)
            }.build()
        }

        fun getInstance(cacheFile : Cache): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient(cacheFile))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}