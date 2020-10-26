package com.example.currencyexchangeapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class HostViewModel : ViewModel() {
    private val _curse = MutableLiveData<List<CryptoCurrencyByDay>>()
    val curse: LiveData<List<CryptoCurrencyByDay>>
        get() = _curse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getNews(): LiveData<List<CryptoCurrencyByDay>> {
        return _curse
    }

    fun getCurrencyByDay(date: String): CryptoCurrencyByDay? {
        return curse.value!!.find { currency -> currency.date.equals(date) }
    }

    init {
        //loadCurrency()
        //loadNews()
    }

//    private fun loadCurrency() {
//        viewModelScope.launch {
//            try {
//                val currencyResponse = Api.Service.getCurse()
//                _curse.postValue(currencyResponse.cryptoCurrencyName.value)
//            } catch (e: Exception) {
//                _error.postValue(e.toString())
//            }
//        }
//    }

//    fun loadNews() {
//        viewModelScope.launch {
//            val response = Api.Service.getCurse()
//            _curse.value = response.cryptoCurrencyName.value
//            //_curse.value = response.body()?.articles
//        }
//    }
}
