package com.nasa.astronomypicture.model.api

import android.content.Context
import com.nasa.astronomypicture.Utils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {

        const val API_KEY = "kH0OQm8bmOBiWMWWUF6UbLmJYmwof6LiKG6bEDkf"
        const val BASE_URL: String = "https://api.nasa.gov/planetary/"

        fun getInstance(context: Context): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private fun getClient(context: Context): OkHttpClient {
            val cacheFile = Cache(File(context.cacheDir, "apod"), (5 * 1024 * 1024).toLong()) // 5MB
            return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (Utils.isNetworkAvailable(context))
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 10).build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    chain.proceed(request)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .cache(cacheFile)
                .build()
        }
    }
}