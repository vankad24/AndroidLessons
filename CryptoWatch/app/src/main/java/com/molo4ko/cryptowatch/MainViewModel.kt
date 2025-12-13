package com.molo4ko.cryptowatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _price = MutableLiveData<String>()
    val price: LiveData<String> = _price

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> = _history

    fun updatePrice(newPrice: String) {
        _price.value = newPrice

        val currentHistory = _history.value.orEmpty().toMutableList()
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        currentHistory.add(0, "$timestamp: $newPrice")
        _history.value = currentHistory
    }
}