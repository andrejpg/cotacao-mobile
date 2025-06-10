package com.example.cotacaomobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : AppCompatActivity() {

    private lateinit var convMoedaAPI: ConvMoedaAPI
    private lateinit var converterLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val extratoReais = findViewById<Button>(R.id.extratoReais)
        val extratoDolares = findViewById<Button>(R.id.extratoDolares)
        val extratoBitcoin = findViewById<Button>(R.id.extratoBitcoin)

        converterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val atualizadoReais = data?.getDoubleExtra("novoExtratoReais", 0.0) ?: 0.0
                val atualizadoDolares = data?.getDoubleExtra("novoExtratoDolares", 0.0) ?: 0.0
                val atualizadoBitcoin = data?.getDoubleExtra("novoExtratoBitcoin", 0.0) ?: 0.0

                Log.d("MainActivity", "Novo BRL: $atualizadoReais, USD: $atualizadoDolares, BTC: $atualizadoBitcoin")
                extratoReais.text = "R$%.2f".format(atualizadoReais)
                extratoDolares.text = "U$%.2f".format(atualizadoDolares)
                extratoBitcoin.text = "%.4f BTC".format(atualizadoBitcoin)
            } else Log.d("MainActivity", "Sem dados")
        }


        val btnNovaConversao = findViewById<Button>(R.id.btnConverter)
        btnNovaConversao.setOnClickListener {
            val intent = Intent(this, ConverterActivity::class.java).apply {
                putExtra("extratoReais", 100000.0)
                    .apply { putExtra("extratoDolares", 50000.0) }
                    .apply { putExtra("extratoBitcoin", 0.5) }
            }
            converterLauncher.launch(intent)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        convMoedaAPI = retrofit.create(ConvMoedaAPI::class.java)
    }
}