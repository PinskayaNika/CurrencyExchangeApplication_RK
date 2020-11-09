package com.example.currencyexchangeapplication

data class CryptoCurrency(
    val cryptoCurrencyName:String,
    val cryptoCurrencyByDays: ArrayList<CryptoCurrencyByDay>
) {
}