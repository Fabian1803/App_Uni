package com.example.appuni.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val studentId = MutableLiveData<Long>()
}
