package com.example.cotacaomobile

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConvRecActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var convMoedaAPI: ConvMoedaAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conv_rec)

        progressBar = findViewById(R.id.progressBar)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/json/last/:moedas")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        convMoedaAPI = retrofit.create(ConvMoedaAPI::class.java)
    }

    
}