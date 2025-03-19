package com.zybooks.lockedin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zybooks.lockedin.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    loadFragment(SettingsFragment())
                    return@setOnItemSelectedListener false
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment?.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bottomNavigationView.isInitialized) {
            updateNavSelection()
        }
    }

    private fun updateNavSelection() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (currentFragment) {
            is TimerFragment -> bottomNavigationView.selectedItemId = R.id.nav_home
            is HistoryFragment -> bottomNavigationView.selectedItemId = R.id.nav_history
            is SettingsFragment -> bottomNavigationView.selectedItemId = R.id.nav_settings
        }
    }
}
