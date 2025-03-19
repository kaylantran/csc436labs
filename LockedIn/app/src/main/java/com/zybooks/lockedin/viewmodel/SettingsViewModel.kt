package com.zybooks.lockedin.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val studyDuration = MutableLiveData<Long>(1500L)
    val breakDuration = MutableLiveData<Long>(300L)

    fun saveSettings(context: Context) {
        val prefs = context.getSharedPreferences("FocusTimerPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putLong("study_duration", studyDuration.value ?: 1500L)
            putLong("break_duration", breakDuration.value ?: 300L)
            apply()
        }
    }
}
