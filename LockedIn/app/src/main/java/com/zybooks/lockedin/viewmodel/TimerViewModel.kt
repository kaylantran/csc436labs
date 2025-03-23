package com.zybooks.lockedin.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zybooks.lockedin.service.TimerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    val timerValue = MutableLiveData<Long>()
    val isRunning = MutableLiveData<Boolean>(false)

    fun loadTimerState() {
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val endTime = prefs.getLong("timerEndTime", -1L)
        val currentTime = System.currentTimeMillis()

        if (endTime > currentTime) {
            val remaining = (endTime - currentTime) / 1000
            timerValue.value = remaining
            isRunning.value = true
            viewModelScope.launch {
                while (isRunning.value == true && (timerValue.value ?: 0L) > 0) {
                    delay(1000)
                    val current = timerValue.value ?: 0L
                    timerValue.value = if (current > 1) current - 1 else 0L
                }
                isRunning.value = false
            }
        } else {
            val hours = prefs.getInt("studyHours", 0)
            val minutes = prefs.getInt("studyMinutes", 25)
            val seconds = prefs.getInt("studySeconds", 0)
            val defaultSeconds = (hours * 3600 + minutes * 60 + seconds).toLong()
            timerValue.value = defaultSeconds
            isRunning.value = false
        }
    }

    fun startTimer() {
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val hours = prefs.getInt("studyHours", 0)
        val minutes = prefs.getInt("studyMinutes", 25)
        val seconds = prefs.getInt("studySeconds", 0)
        val totalSeconds = (hours * 3600 + minutes * 60 + seconds).toLong()
        val endTime = System.currentTimeMillis() + totalSeconds * 1000
        prefs.edit().putLong("timerEndTime", endTime).apply()

        timerValue.value = totalSeconds
        isRunning.value = true

        viewModelScope.launch {
            while (isRunning.value == true && (timerValue.value ?: 0L) > 0) {
                delay(1000)
                timerValue.value = timerValue.value?.minus(1)
            }
            isRunning.value = false
        }
    }

    fun resetTimer() {
        isRunning.value = false
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        prefs.edit().remove("timerEndTime").apply()
        val hours = prefs.getInt("studyHours", 0)
        val minutes = prefs.getInt("studyMinutes", 25)
        val seconds = prefs.getInt("studySeconds", 0)
        val defaultDuration = (hours * 3600 + minutes * 60 + seconds).toLong()
        timerValue.value = defaultDuration
    }


    fun pauseTimer() {
        isRunning.value = false
    }
}
