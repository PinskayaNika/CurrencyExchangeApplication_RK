package com.example.currencyexchangeapplication

import java.util.*

data class CryptoCurrencyByDay(
    val date: Long,
    val value: Float,
    val fromCurrency: String,
    val toCurrency: String,
    val minOfDay: Float,
    val maxOfDay: Float
) {
}