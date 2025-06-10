package com.example.cotacaomobile
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConvMoeda(
    val real: Double,
    val dolar: Double,
    val bitcoin: Double
): Parcelable
