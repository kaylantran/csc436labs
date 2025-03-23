package com.zybooks.lockedin.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zybooks.lockedin.R
import com.zybooks.lockedin.service.TimerService

class SettingsActivity : AppCompatActivity() {

    private lateinit var studyHours: EditText
    private lateinit var studyMinutes: EditText
    private lateinit var studySeconds: EditText
    private lateinit var breakHours: EditText
    private lateinit var breakMinutes: EditText
    private lateinit var breakSeconds: EditText
    private lateinit var bannerNotification: SwitchMaterial
    private lateinit var fullscreenNotification: SwitchMaterial
    private lateinit var backgroundTimer: SwitchMaterial
    private lateinit var saveButton: Button
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)

        studyHours = findViewById(R.id.study_hours)
        studyMinutes = findViewById(R.id.study_minutes)
        studySeconds = findViewById(R.id.study_seconds)
        breakHours = findViewById(R.id.break_hours)
        breakMinutes = findViewById(R.id.break_minutes)
        breakSeconds = findViewById(R.id.break_seconds)
        bannerNotification = findViewById(R.id.banner_notification)
        fullscreenNotification = findViewById(R.id.fullscreen_notification)
        saveButton = findViewById(R.id.save_button)

        loadSettings()

        saveButton.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun loadSettings() {
        studyHours.setText(preferences.getInt("studyHours", 0).toString())
        studyMinutes.setText(preferences.getInt("studyMinutes", 10).toString())
        studySeconds.setText(preferences.getInt("studySeconds", 0).toString())

        breakHours.setText(preferences.getInt("breakHours", 0).toString())
        breakMinutes.setText(preferences.getInt("breakMinutes", 5).toString())
        breakSeconds.setText(preferences.getInt("breakSeconds", 0).toString())

        bannerNotification.isChecked = preferences.getBoolean("bannerNotification", false)
        fullscreenNotification.isChecked = preferences.getBoolean("fullscreenNotification", false)
    }

    private fun saveSettings() {
        val editor = preferences.edit()

        editor.putInt("studyHours", studyHours.text.toString().toIntOrNull() ?: 0)
        editor.putInt("studyMinutes", studyMinutes.text.toString().toIntOrNull() ?: 10)
        editor.putInt("studySeconds", studySeconds.text.toString().toIntOrNull() ?: 0)

        editor.putInt("breakHours", breakHours.text.toString().toIntOrNull() ?: 0)
        editor.putInt("breakMinutes", breakMinutes.text.toString().toIntOrNull() ?: 5)
        editor.putInt("breakSeconds", breakSeconds.text.toString().toIntOrNull() ?: 0)

        editor.putBoolean("bannerNotification", bannerNotification.isChecked)
        editor.putBoolean("fullscreenNotification", fullscreenNotification.isChecked)
        editor.apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from_settings", true)
        startActivity(intent)
        finish()
    }
}
