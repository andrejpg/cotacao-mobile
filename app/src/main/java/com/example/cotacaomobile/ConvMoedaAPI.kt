package com.example.cotacaomobile

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ConvMoedaAPI {

    @GET("/{moedaOrigem}-{moedaDestino}")
    suspend fun getConvMoeda(
        @Path("moedaOrigem") moedaOrigem: String,
        @Path("moedaDestino") moedaDestino: String
    ): Response<Map<String, MoedaValor>>
}

data class MoedaValor(
    val bid: String
)