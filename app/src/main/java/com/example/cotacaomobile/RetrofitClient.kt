package com.example.cotacaomobile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val api: ConvMoedaAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConvMoedaAPI::class.java)
    }
}