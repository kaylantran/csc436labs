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
    val isRunning = MutableLiveData<Boolean>()
    val sessionLabel = MutableLiveData<String>()
    private var countDownActive = false

    fun loadTimerState() {
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val endTime = prefs.getLong("timerEndTime", -1)
        val currentTime = System.currentTimeMillis()
        if (endTime > currentTime) {
            isRunning.value = true
            startCountdown()
        } else {
            resetToDefaultDuration()
            isRunning.value = false
        }
    }

    fun startTimer(session: String = "WORK") {
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val paused = prefs.getLong("pausedTime", -1L)
        val totalSeconds = if (paused > 0) {
            prefs.edit().remove("pausedTime").apply()
            paused
        } else {
            val hours = prefs.getInt("studyHours", 0)
            val minutes = prefs.getInt("studyMinutes", 25)
            val seconds = prefs.getInt("studySeconds", 0)
            (hours * 3600 + minutes * 60 + seconds).toLong()
        }
        val endTime = System.currentTimeMillis() + totalSeconds * 1000
        prefs.edit().putLong("timerEndTime", endTime).apply()
        timerValue.value = totalSeconds
        isRunning.value = true
        startCountdown()
    }

    private fun startCountdown() {
        countDownActive = true
        viewModelScope.launch {
            while (countDownActive) {
                val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
                val endTime = prefs.getLong("timerEndTime", -1)
                val currentTime = System.currentTimeMillis()
                val remaining = (endTime - currentTime) / 1000

                val sessionType = prefs.getString("currentSessionType", "WORK")
                sessionLabel.postValue(
                    if (sessionType == "WORK") "Time remaining until next break:" else "Break ends in:"
                )

                if (endTime == -1L || remaining <= 0) {
                    if (isRunning.value == true) {
                        timerValue.postValue(0L)
                        isRunning.postValue(false)
                    }
                } else {
                    timerValue.postValue(remaining)
                    if (isRunning.value != true) {
                        isRunning.postValue(true)
                    }
                }
                delay(1000)
            }
        }
    }


    fun resetTimer() {
        isRunning.value = false
        val context = getApplication<Application>().applicationContext
        context.stopService(Intent(context, TimerService::class.java))
        val prefs = context.getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        prefs.edit().remove("timerEndTime").apply()
        resetToDefaultDuration()
    }

    private fun resetToDefaultDuration() {
        val prefs = getApplication<Application>().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val hours = prefs.getInt("studyHours", 0)
        val minutes = prefs.getInt("studyMinutes", 25)
        val seconds = prefs.getInt("studySeconds", 0)
        val defaultDuration = (hours * 3600 + minutes * 60 + seconds).toLong()

        timerValue.value = defaultDuration
        isRunning.value = false
    }

    fun pauseTimer() {
        val remaining = timerValue.value ?: 0L
        countDownActive = false
        isRunning.value = false
        val context = getApplication<Application>().applicationContext
        context.stopService(Intent(context, TimerService::class.java))
        val prefs = context.getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putLong("pausedTime", remaining)
            .remove("timerEndTime")
            .apply()
    }

}
