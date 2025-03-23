package com.zybooks.lockedin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zybooks.lockedin.R
import com.zybooks.lockedin.util.NotificationHelper

enum class TimerSession {
    WORK, BREAK
}

class TimerService : Service() {

    private var timer: CountDownTimer? = null
    private var currentSession = TimerSession.WORK
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duration = intent?.getLongExtra("TIMER_DURATION", 1500000L) ?: 1500000L
        val sessionType = intent?.getStringExtra("SESSION_TYPE") ?: "WORK"
        currentSession = TimerSession.valueOf(sessionType)

        Log.d("TimerService", "Starting $currentSession session for ${duration / 1000} seconds")

        startTimer(duration)
        return START_STICKY
    }

    private fun startTimer(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        prefs.edit()
            .putString("currentSessionType", currentSession.name)
            .putLong("timerEndTime", endTime)
            .apply()
        startForegroundWithNotification(currentSession)
        timer?.cancel()
        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                prefs.edit().putLong("remainingTime", secondsLeft).apply()
            }
            override fun onFinish() {
                prefs.edit()
                    .remove("timerEndTime")
                    .remove("remainingTime")
                    .apply()
                val message = if (currentSession == TimerSession.WORK) {
                    "Work session complete! Time for a break."
                } else {
                    "Break is over. Time to get back to work!"
                }
                NotificationHelper.sendNotification(
                    this@TimerService,
                    "Session complete",
                    message
                )
                currentSession = getNextSession()
                val nextDuration = getDurationForSession(currentSession)
                startTimer(nextDuration)
            }
        }.start()
    }

    private fun getDurationForSession(session: TimerSession): Long {
        return when (session) {
            TimerSession.WORK -> {
                val hours = prefs.getInt("studyHours", 0)
                val minutes = prefs.getInt("studyMinutes", 25)
                val seconds = prefs.getInt("studySeconds", 0)
                (hours * 3600 + minutes * 60 + seconds) * 1000L
            }
            TimerSession.BREAK -> {
                val minutes = prefs.getInt("breakMinutes", 5)
                val seconds = prefs.getInt("breakSeconds", 0)
                (minutes * 60 + seconds) * 1000L
            }
        }
    }

    private fun getNextSession(): TimerSession {
        return if (currentSession == TimerSession.WORK) TimerSession.BREAK else TimerSession.WORK
    }

    private fun startForegroundWithNotification(session: TimerSession) {
        val channelId = "TIMER_SERVICE_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Locked In: ${session.name.capitalize()} Session")
            .setContentText("Your ${session.name.toLowerCase()} session is in progress.")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()

        startForeground(2, notification)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
