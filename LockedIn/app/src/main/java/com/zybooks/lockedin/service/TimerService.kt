package com.zybooks.lockedin.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.zybooks.lockedin.util.NotificationHelper

class TimerService : Service() {

    private var timer: CountDownTimer? = null
    private var remainingTime: Long = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalTime = intent?.getLongExtra("TIMER_DURATION", 1500000) ?: 1500000

        timer?.cancel()

        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                Log.d("TimerService", "Time left: ${millisUntilFinished / 1000} seconds")
            }

            override fun onFinish() {
                Log.d("TimerService", "Timer finished")
                NotificationHelper.sendNotification(this@TimerService, "Study Session Complete!", "Time to take a break!")
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
