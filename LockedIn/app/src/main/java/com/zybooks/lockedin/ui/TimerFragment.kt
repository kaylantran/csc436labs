package com.zybooks.lockedin.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zybooks.lockedin.R
import com.zybooks.lockedin.viewmodel.TimerViewModel

class TimerFragment : Fragment() {
    private lateinit var viewModel: TimerViewModel
    private lateinit var timerText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var resetButton: ImageButton

    private var countdownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 600000  // Default: 10 minutes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        timerText = view.findViewById(R.id.timer_text)
        playButton = view.findViewById(R.id.play_button)
        resetButton = view.findViewById(R.id.reset_button)

        // Observe timer updates from ViewModel
        viewModel.timerValue.observe(viewLifecycleOwner) { newTime ->
            timerText.text = formatTime(newTime)
        }

        playButton.setOnClickListener {
            if (timerRunning) pauseTimer() else startTimer()
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        return view
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timerText.text = formatTime(timeLeftInMillis / 1000)
            }

            override fun onFinish() {
                timerRunning = false
                playButton.setImageResource(R.drawable.ic_play)
            }
        }.start()

        playButton.setImageResource(R.drawable.ic_pause)
        timerRunning = true
    }

    private fun pauseTimer() {
        countdownTimer?.cancel()
        playButton.setImageResource(R.drawable.ic_play)
        timerRunning = false
    }

    private fun resetTimer() {
        countdownTimer?.cancel()
        timeLeftInMillis = 600000  // Reset to 10 minutes
        timerText.text = formatTime(timeLeftInMillis / 1000)
        playButton.setImageResource(R.drawable.ic_play)
        timerRunning = false
    }

    private fun formatTime(seconds: Long): String {
        val minutes = (seconds / 60) % 60
        val hours = seconds / 3600
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }
}
