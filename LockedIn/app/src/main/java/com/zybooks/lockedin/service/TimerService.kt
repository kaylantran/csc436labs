package com.zybooks.lockedin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.zybooks.lockedin.R
import com.zybooks.lockedin.util.NotificationHelper

class TimerService : Service() {

    private var timer: CountDownTimer? = null
    private var remainingTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalTime = intent?.getLongExtra("TIMER_DURATION", 1500000) ?: 1500000
        val endTime = System.currentTimeMillis() + totalTime
        val prefs = getSharedPreferences("LockedInPrefs", MODE_PRIVATE)
        prefs.edit().putLong("timerEndTime", endTime).apply()
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
            .setContentTitle("Locked In Timer Running")
            .setContentText("Your study session is in progress.")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()

        startForeground(
            2,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )


        timer?.cancel()
        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                Log.d("TimerService", "Time left: ${millisUntilFinished / 1000} seconds")
            }
            override fun onFinish() {
                Log.d("TimerService", "Timer finished")
                prefs.edit().remove("timerEndTime").apply()
                NotificationHelper.sendNotification(
                    this@TimerService,
                    "Study Session Complete!",
                    "Time to take a break!"
                )
                stopForeground(true)
                stopSelf()
            }
        }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
