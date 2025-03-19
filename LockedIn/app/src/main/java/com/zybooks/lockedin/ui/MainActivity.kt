package com.zybooks.lockedin.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zybooks.lockedin.R

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var resetButton: ImageButton
    private var timerRunning = false
    private var countdownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 600000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timer_text)
        playButton = findViewById(R.id.play_button)
        resetButton = findViewById(R.id.reset_button)

        updateTimerText()

        playButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                timerRunning = false
                updatePlayButtonIcon(false)
            }
        }.start()

        updatePlayButtonIcon(true)
        timerRunning = true
    }

    private fun pauseTimer() {
        countdownTimer?.cancel()
        updatePlayButtonIcon(false)
        timerRunning = false
    }


    private fun resetTimer() {
        countdownTimer?.cancel()
        timeLeftInMillis = 600000
        updateTimerText()
        updatePlayButtonIcon(false)
        playButton.setImageResource(R.drawable.ic_play)
        timerRunning = false
    }

    private fun updatePlayButtonIcon(isPlaying: Boolean) {
        playButton.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)

        playButton.scaleType = ImageView.ScaleType.FIT_CENTER
        playButton.adjustViewBounds = true
        playButton.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        playButton.requestLayout()
        playButton.invalidate()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }
}
