package com.zybooks.lockedin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zybooks.lockedin.R
import com.zybooks.lockedin.service.TimerService
import com.zybooks.lockedin.viewmodel.TimerViewModel

class TimerFragment : Fragment() {

    private lateinit var viewModel: TimerViewModel
    private lateinit var timerText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var sessionTypeText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        timerText = view.findViewById(R.id.timerTextView)
        playButton = view.findViewById(R.id.play_button)
        resetButton = view.findViewById(R.id.reset_button)
        sessionTypeText = view.findViewById(R.id.sessionTypeTextView)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(TimerViewModel::class.java)

        viewModel.loadTimerState()

        viewModel.timerValue.observe(viewLifecycleOwner) { seconds ->
            timerText.text = formatTime(seconds)
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { running ->
            updatePlayButtonIcon(running)
        }

        viewModel.sessionLabel.observe(viewLifecycleOwner) { label ->
            sessionTypeText.text = label
        }

        playButton.setOnClickListener {
            if (viewModel.isRunning.value == true) {
                viewModel.pauseTimer()
            } else {
                val prefs = requireContext().getSharedPreferences("LockedInPrefs", Context.MODE_PRIVATE)
                val pausedTime = prefs.getLong("pausedTime", -1L)
                val duration = if (pausedTime > 0) {
                    pausedTime * 1000
                } else {
                    val hours = prefs.getInt("studyHours", 0)
                    val minutes = prefs.getInt("studyMinutes", 25)
                    val seconds = prefs.getInt("studySeconds", 0)
                    (hours * 3600 + minutes * 60 + seconds) * 1000L
                }
                val intent = Intent(requireContext(), TimerService::class.java).apply {
                    putExtra("TIMER_DURATION", duration)
                    putExtra("SESSION_TYPE", "WORK")
                }
                ContextCompat.startForegroundService(requireContext(), intent)
                viewModel.startTimer("WORK")
            }
        }

        resetButton.setOnClickListener {
            viewModel.resetTimer()
        }

        return view
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    private fun updatePlayButtonIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playButton.setImageResource(icon)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTimerState()

        viewModel.isRunning.value?.let {
            updatePlayButtonIcon(it)
        }
    }
}
