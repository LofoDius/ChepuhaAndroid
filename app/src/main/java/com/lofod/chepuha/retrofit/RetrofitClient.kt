package com.lofod.chepuha.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var client: Retrofit? = null

    fun getClient(): Retrofit {
        if (client == null) {
            client = Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return client!!
    }
}