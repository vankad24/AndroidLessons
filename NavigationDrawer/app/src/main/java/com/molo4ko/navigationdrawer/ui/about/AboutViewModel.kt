package com.molo4ko.navigationdrawer.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Это фрагмент О приложении\n\nВерсия 1.0\nИркутский государственный университет"
    }
    val text: LiveData<String> = _text
}