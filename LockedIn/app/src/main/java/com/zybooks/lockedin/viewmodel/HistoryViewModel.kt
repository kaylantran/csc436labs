package com.zybooks.lockedin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {
    val sessionHistory: MutableLiveData<List<String>> = MutableLiveData(listOf())

    fun addSession(session: String) {
        sessionHistory.value = sessionHistory.value?.plus(session)
    }
}
