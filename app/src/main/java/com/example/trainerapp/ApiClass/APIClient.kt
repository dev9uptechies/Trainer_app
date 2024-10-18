package com.example.trainerapp.ApiClass

import android.content.Context
import com.example.trainerapp.PreferencesManager
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

class APIClient(context: Context) {
    private var retrofit: Retrofit? = null
    private var token: String? = null
    private var preferencesManager: PreferencesManager = PreferencesManager(context)
    fun client(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        token =  preferencesManager.getToken()
//        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val client = OkHttpClient.Builder().addInterceptor { chain ->

            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer "+token)
                .build()
            chain.proceed(newRequest)
        }.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl("https://trainers.codefriend.in/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit as Retrofit
    }
}