package com.example.cotacaomobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : AppCompatActivity() {

    private lateinit var convMoedaAPI: ConvMoedaAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/json/last/:moedas")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        convMoedaAPI = retrofit.create(ConvMoedaAPI::class.java)
    }
}