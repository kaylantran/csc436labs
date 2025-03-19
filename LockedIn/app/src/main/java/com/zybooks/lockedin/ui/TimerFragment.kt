package com.zybooks.lockedin.ui

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
    private var timeLeftInMillis: Long = (25*60000 )
    private lateinit var timerTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        timerText = view.findViewById(R.id.timerTextView)
        playButton = view.findViewById(R.id.play_button)
        resetButton = view.findViewById(R.id.reset_button)

        viewModel.timerValue.observe(viewLifecycleOwner) { newTime ->
            timerText.text = formatTime(newTime)
        }

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
                updatePlayButtonIcon(false)
            }
        }.start()

        timerRunning = true
        updatePlayButtonIcon(true)
    }

    private fun pauseTimer() {
        countdownTimer?.cancel()
        timerRunning = false
        updatePlayButtonIcon(false)
    }

    private fun resetTimer() {
        countdownTimer?.cancel()
        timeLeftInMillis = (25*60000)
        timerText.text = formatTime(timeLeftInMillis / 1000)
        timerRunning = false
        updatePlayButtonIcon(false)
    }

    private fun updatePlayButtonIcon(isPlaying: Boolean) {
        Log.d("TIMER_FRAGMENT", "Updating play button icon: $isPlaying")
        Log.d("TIMER_FRAGMENT", "playButton reference: $playButton")

        requireActivity().runOnUiThread {
            if (::playButton.isInitialized) {
                val newIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                playButton.setImageDrawable(requireContext().getDrawable(newIcon))

                playButton.invalidate()
                playButton.requestLayout()
            } else {
                Log.e("TIMER_FRAGMENT", "playButton is not initialized!")
            }
        }
    }

    private fun startNewTimer(hours: Int, minutes: Int, seconds: Int) {
        val durationInMillis = ((hours * 3600 + minutes * 60 + seconds) * 1000).toLong()
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / 1000) / 3600
                val mins = ((millisUntilFinished / 1000) % 3600) / 60
                val secs = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("%02d:%02d:%02d", hours, mins, secs)
            }
            override fun onFinish() {
                timerText.text = "00:00:00"
            }
        }.start()
    }


    fun updateTimer(hours: Int, minutes: Int, seconds: Int) {
        timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        countdownTimer?.cancel()
        startNewTimer(hours, minutes, seconds)
    }

    private fun formatTime(seconds: Long): String {
        val minutes = (seconds / 60) % 60
        val hours = seconds / 3600
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireContext().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val studyHours = sharedPreferences.getInt("studyHours", 0)
        val studyMinutes = sharedPreferences.getInt("studyMinutes", 10)
        val studySeconds = sharedPreferences.getInt("studySeconds", 0)

        timeLeftInMillis = ((studyHours * 3600 + studyMinutes * 60 + studySeconds) * 1000).toLong()

        Log.d("TIMER_FRAGMENT", "Updated study time on resume: ${formatTime(timeLeftInMillis / 1000)}")

        timerText.text = formatTime(timeLeftInMillis / 1000)
    }

    fun updateTimerFromSettings() {
        val sharedPreferences = requireContext().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
        val studyHours = sharedPreferences.getInt("studyHours", 0)
        val studyMinutes = sharedPreferences.getInt("studyMinutes", 10)
        val studySeconds = sharedPreferences.getInt("studySeconds", 0)

        timeLeftInMillis = ((studyHours * 3600 + studyMinutes * 60 + studySeconds) * 1000).toLong()

        Log.d("TIMER_FRAGMENT", "Manually updated study time: ${formatTime(timeLeftInMillis / 1000)}")

        timerText.text = formatTime(timeLeftInMillis / 1000)
    }


}
