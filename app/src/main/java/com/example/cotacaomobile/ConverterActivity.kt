package com.example.cotacaomobile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConverterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_converter)

        val moedaInicial1 = findViewById<Chip>(R.id.chipOpcaoInicial1)
        val moedaInicial2 = findViewById<Chip>(R.id.chipOpcaoInicial2)
        val moedaInicial3 = findViewById<Chip>(R.id.chipOpcaoInicial3)
        val inputValor = findViewById<EditText>(R.id.editTextValorInicial)
        val moedaFinal1 = findViewById<Chip>(R.id.chipOpcaoFinal1)
        val moedaFinal2 = findViewById<Chip>(R.id.chipOpcaoFinal2)
        val moedaFinal3 = findViewById<Chip>(R.id.chipOpcaoFinal3)
        val btnConverter = findViewById<Button>(R.id.btnSubmitConversao)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputValor.addTextChangedListener(object : TextWatcher {
            private var currentString = ""

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentString) {
                    inputValor.removeTextChangedListener(this)
                    val clean = s.toString().replace("[^\\d]".toRegex(), "")
                    val parsed = clean.toDoubleOrNull() ?: 0.0
                    val formatted = java.text.NumberFormat.getNumberInstance(java.util.Locale("pt", "BR"))
                        .format(parsed/100)
                    currentString = formatted
                    inputValor.setText(formatted)
                    inputValor.setSelection(formatted.length)
                    inputValor.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnConverter.setOnClickListener {
            val valorString = inputValor.text.toString()
                .replace(".", "")
                .replace(",", ".")
            val valor = valorString.toDoubleOrNull() ?: 0.0

            val moedaInicial = when {
                moedaInicial1.isChecked -> "BRL"
                moedaInicial2.isChecked -> "USD"
                moedaInicial3.isChecked -> "BTC"
                else -> "DESCONHECIDO"
            }

            val moedaFinal = when {
                moedaFinal1.isChecked -> "BRL"
                moedaFinal2.isChecked -> "USD"
                moedaFinal3.isChecked -> "BTC"
                else -> "DESCONHECIDO"
            }

            Log.d("debug", "Moeda inicial: $moedaInicial")
            Log.d("debug", "Moeda final: $moedaFinal")
            Log.d("debug", "Valor: $valor")

            if (valorString.isEmpty() || moedaInicial == "DESCONHECIDO" || moedaFinal == "DESCONHECIDO") {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                //mandar pra API
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.api.getConvMoeda(moedaInicial, moedaFinal)

                        if (response.isSuccessful) {
                            val map = response.body()
                            //val key = moedaInicial + moedaFinal
                            //val bid = map?.get(key)?.bid?.toDoubleOrNull() ?: 0.0
                            Log.d("debug", "Resposta da API: $map")

                            //val valorConvertido = valor * bid
                            val valorConvertido = 10

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@ConverterActivity,
                                    "Valor convertido: %.2f".format(valorConvertido),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ConverterActivity, "Erro da API: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("API_EXCEPTION", e.toString())
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ConverterActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}