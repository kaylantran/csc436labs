package com.zybooks.lockedin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    val timerValue = MutableLiveData<Long>(1500L)
    val isRunning = MutableLiveData<Boolean>(false)

    fun startTimer() {
        isRunning.value = true
        // Timer logic using coroutine (simplified)
        viewModelScope.launch {
            while (isRunning.value == true && timerValue.value!! > 0) {
                delay(1000)
                timerValue.value = timerValue.value?.minus(1)
            }
        }
    }

    fun resetTimer() {
        timerValue.value = 1500L
        isRunning.value = false
    }
}
