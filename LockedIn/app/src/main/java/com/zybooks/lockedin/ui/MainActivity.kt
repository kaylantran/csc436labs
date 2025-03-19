package com.zybooks.lockedin.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zybooks.lockedin.R
import com.zybooks.lockedin.service.TimerService
import com.zybooks.lockedin.util.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("from_settings", false)) {
            val timerFragment = supportFragmentManager.findFragmentById(R.id.timerFragment) as? TimerFragment
            timerFragment?.updateTimerFromSettings()
        }

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
                    return@setOnItemSelectedListener true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        if (::bottomNavigationView.isInitialized) {
            updateNavSelection()
        }
        applySettings()
    }

    private fun updateNavSelection() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (currentFragment) {
            is TimerFragment -> bottomNavigationView.selectedItemId = R.id.nav_home
            is HistoryFragment -> bottomNavigationView.selectedItemId = R.id.nav_history
            else -> R.id.nav_home
        }
    }

    private fun applySettings() {
        val preferences = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)

        val studyHours = preferences.getInt("studyHours", 0)
        val studyMinutes = preferences.getInt("studyMinutes", 25)
        val studySeconds = preferences.getInt("studySeconds", 0)

        val breakHours = preferences.getInt("breakHours", 0)
        val breakMinutes = preferences.getInt("breakMinutes", 5)
        val breakSeconds = preferences.getInt("breakSeconds", 0)

        val bannerNotification = preferences.getBoolean("bannerNotification", false)
        val fullscreenNotification = preferences.getBoolean("fullscreenNotification", false)
        val backgroundTimer = preferences.getBoolean("backgroundTimer", false)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment is TimerFragment) {
            currentFragment.updateTimer(studyHours, studyMinutes, studySeconds)
        }

        if (backgroundTimer) {
            startBackgroundTimerService()
        } else {
            stopBackgroundTimerService()
        }

        if (bannerNotification) {
            NotificationHelper.sendNotification(this, "Banner Notification Enabled", "You'll receive alerts.")
        }
        if (fullscreenNotification) {
            NotificationHelper.sendNotification(this, "Fullscreen Alerts Enabled", "You will get full-screen notifications.")
        }
    }

    private fun startBackgroundTimerService() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("TIMER_DURATION", (25 * 60 * 1000).toLong())
        startService(intent)
    }

    private fun stopBackgroundTimerService() {
        val intent = Intent(this, TimerService::class.java)
        stopService(intent)
    }

}
