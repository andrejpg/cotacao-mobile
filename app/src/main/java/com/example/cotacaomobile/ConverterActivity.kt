package com.example.cotacaomobile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
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
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class ConverterActivity : AppCompatActivity() {
    private var disponivelReais: Double = 0.0
    private var disponivelDolares: Double = 0.0
    private var disponivelBitcoin: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_converter)

        disponivelReais = if (intent.hasExtra("novoExtratoReais"))
            intent.getDoubleExtra("novoExtratoReais", 0.0)
        else
            intent.getDoubleExtra("extratoReais", 0.0)
        disponivelDolares = if (intent.hasExtra("novoExtratoDolares"))
            intent.getDoubleExtra("novoExtratoDolares", 0.0)
        else
            intent.getDoubleExtra("extratoDolares", 0.0)
        disponivelBitcoin = if (intent.hasExtra("novoExtratoBitcoin"))
            intent.getDoubleExtra("novoExtratoBitcoin", 0.0)
        else
            intent.getDoubleExtra("extratoBitcoin", 0.0)

        val progressBar = findViewById<ProgressBar>(R.id.progressBarConversao)
        val moedaInicial1 = findViewById<Chip>(R.id.chipOpcaoInicial1)
        val moedaInicial2 = findViewById<Chip>(R.id.chipOpcaoInicial2)
        val moedaInicial3 = findViewById<Chip>(R.id.chipOpcaoInicial3)
        val inputValor = findViewById<EditText>(R.id.editTextValorInicial)
        val moedaFinal1 = findViewById<Chip>(R.id.chipOpcaoFinal1)
        val moedaFinal2 = findViewById<Chip>(R.id.chipOpcaoFinal2)
        val moedaFinal3 = findViewById<Chip>(R.id.chipOpcaoFinal3)
        val btnConverter = findViewById<Button>(R.id.btnSubmitConversao)
        val resultado = findViewById<TextView>(R.id.textViewResultadoFinal)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var currentWatcher: TextWatcher? = null

        fun setupInputWatcher(currencyCode: String) {
            currentWatcher?.let { inputValor.removeTextChangedListener(it) }

            val locale = when (currencyCode) {
                "USD", "BTC" -> Locale.US
                else -> Locale("pt", "BR")
            }

            val maxDecimals = when (currencyCode) {
                "BTC" -> 4
                else -> 2
            }

            val watcher = object : TextWatcher {
                private var isEditing = false

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing || s == null) return
                    isEditing = true

                    try {
                        val clean = s.toString().replace("[^\\d]".toRegex(), "")
                        val parsed = if (clean.isNotEmpty()) clean.toLong() else 0L

                        val factor = Math.pow(10.0, maxDecimals.toDouble())
                        val value = parsed / factor

                        val nf = NumberFormat.getNumberInstance(locale)
                        nf.minimumFractionDigits = maxDecimals
                        nf.maximumFractionDigits = maxDecimals

                        val formatted = nf.format(value)

                        inputValor.setText(formatted)
                        inputValor.setSelection(formatted.length)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    isEditing = false
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            inputValor.addTextChangedListener(watcher)
            currentWatcher = watcher
        }

        moedaInicial1.setOnClickListener { setupInputWatcher("BRL") }
        moedaInicial2.setOnClickListener { setupInputWatcher("USD") }
        moedaInicial3.setOnClickListener { setupInputWatcher("BTC") }

        val selectedLocale = when {
            moedaInicial1.isChecked -> Locale("pt", "BR")
            else -> Locale.US
        }

        btnConverter.setOnClickListener {
            var moedaInicial = when {
                moedaInicial1.isChecked -> "BRL"
                moedaInicial2.isChecked -> "USD"
                moedaInicial3.isChecked -> "BTC"
                else -> "DESCONHECIDO"
            }

            var moedaFinal = when {
                moedaFinal1.isChecked -> "BRL"
                moedaFinal2.isChecked -> "USD"
                moedaFinal3.isChecked -> "BTC"
                else -> "DESCONHECIDO"
            }

            val textoSujo = inputValor.text.toString()

            val valorString = when (moedaInicial) {
                "BRL" -> textoSujo.replace(".", "").replace(",", ".")
                "USD", "BTC" -> textoSujo.replace(",", "")
                else -> textoSujo
            }
            val valor = valorString.toDoubleOrNull() ?: 0.0

            Log.d("debug", "Moeda inicial: $moedaInicial")
            Log.d("debug", "Moeda final: $moedaFinal")
            Log.d("debug", "Valor: $valor")

            if (valor > disponivelReais && moedaInicial == "BRL") {
                Toast.makeText(this, "Saldo insuficiente em Reais", Toast.LENGTH_SHORT).show()

            } else if (valor > disponivelDolares && moedaInicial == "USD") {
                Toast.makeText(this, "Saldo insuficiente em Dólares", Toast.LENGTH_SHORT).show()
            } else if (valor > disponivelBitcoin && moedaInicial == "BTC") {
                Toast.makeText(this, "Saldo insuficiente em Bitcoin", Toast.LENGTH_SHORT).show()
            } else if (valorString.isEmpty() || moedaInicial == "DESCONHECIDO" || moedaFinal == "DESCONHECIDO") {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                //mandar pra API
                var isBrlToBitcoin: Boolean = false
                var trocaMoedaInicial = ""
                var trocaMoedaFinal = ""
                if (moedaInicial == "BRL" && moedaFinal == "BTC") {
                    trocaMoedaInicial = "BTC";
                    trocaMoedaFinal = "BRL";
                    isBrlToBitcoin = true;
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var response: retrofit2.Response<Map<String, MoedaValor>>
                        if (isBrlToBitcoin) {
                            response = RetrofitClient.api.getConvMoeda(trocaMoedaInicial, trocaMoedaFinal)
                        } else {
                            response = RetrofitClient.api.getConvMoeda(moedaInicial, moedaFinal)
                        }
                        if (response.isSuccessful) {
                            val map = response.body()
                            val key = moedaInicial + moedaFinal
                            Log.d("debug", "Resposta da API: $map")
                            val bid = map?.get(key)?.bid?.toDoubleOrNull() ?: 0.0

                            var valorConvertido = 0.0

                            if (isBrlToBitcoin) {
                                valorConvertido = valor / bid
                                trocaMoedaInicial = "BRL"
                                trocaMoedaFinal = "BTC"
                            }
                            else {valorConvertido = valor * bid}
                            Log.d("debug", "Valor convertido: $valorConvertido")
                            withContext(Dispatchers.Main) {
                                var finalCurrencySymbol = ""
                                if (moedaFinal == "BTC") {
                                    finalCurrencySymbol = "BTC"
                                } else {
                                    val currency = Currency.getInstance(selectedLocale)
                                    finalCurrencySymbol = currency.symbol
                                }
                                resultado.text = finalCurrencySymbol+' '+String.format("%.2f", valorConvertido)
                                progressBar.progress = 100
                                btnConverter.isEnabled = false
                                when (moedaInicial) {
                                    "BRL" -> disponivelReais -= valor
                                    "USD" -> disponivelDolares -= valor
                                    "BTC" -> disponivelBitcoin -= valor
                                }

                                when (moedaFinal) {
                                    "BRL" -> disponivelReais += valorConvertido
                                    "USD" -> disponivelDolares += valorConvertido
                                    "BTC" -> disponivelBitcoin += valorConvertido
                                }

                                val resultIntent = Intent().apply {
                                    putExtra("novoExtratoReais", disponivelReais)
                                    putExtra("novoExtratoDolares", disponivelDolares)
                                    putExtra("novoExtratoBitcoin", disponivelBitcoin)
                                }
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                if (response.code() === 404) {
                                    Toast.makeText(this@ConverterActivity, "Moeda não encontrada", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        this@ConverterActivity,
                                        "Erro da API: ${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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