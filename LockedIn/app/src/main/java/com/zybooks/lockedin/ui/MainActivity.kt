package com.zybooks.lockedin.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zybooks.lockedin.R
import com.zybooks.lockedin.service.TimerService

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            loadFragment(TimerFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(TimerFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bottomNavigationView.isInitialized) {
            updateNavSelection()
        }
        applySettings()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun updateNavSelection() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (currentFragment) {
            is TimerFragment -> bottomNavigationView.selectedItemId = R.id.nav_home
            is HistoryFragment -> bottomNavigationView.selectedItemId = R.id.nav_history
        }
    }

    private fun applySettings() {
        val preferences = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)
        val backgroundTimer = preferences.getBoolean("backgroundTimer", false)

        if (backgroundTimer) {
            startBackgroundTimerService()
        } else {
            stopBackgroundTimerService()
        }
    }

    private fun startBackgroundTimerService() {
        val preferences = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)
        val studyHours = preferences.getInt("studyHours", 0)
        val studyMinutes = preferences.getInt("studyMinutes", 25)
        val studySeconds = preferences.getInt("studySeconds", 0)
        val durationMillis = ((studyHours * 3600 + studyMinutes * 60 + studySeconds) * 1000).toLong()

        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("TIMER_DURATION", durationMillis)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopBackgroundTimerService() {
        val intent = Intent(this, TimerService::class.java)
        stopService(intent)
    }
}
