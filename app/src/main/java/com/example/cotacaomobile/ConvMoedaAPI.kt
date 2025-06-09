package com.example.cotacaomobile

import retrofit2.http.GET

interface ConvMoedaAPI {
    @GET("convert")
    suspend fun getConvMoeda(): ConvMoeda
}