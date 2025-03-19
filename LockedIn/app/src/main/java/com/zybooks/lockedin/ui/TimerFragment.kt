package com.zybooks.lockedin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zybooks.lockedin.R
import com.zybooks.lockedin.viewmodel.TimerViewModel

class TimerFragment : Fragment() {
    private lateinit var viewModel: TimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        val timerText = view.findViewById<TextView>(R.id.timer_text)

        viewModel.timerValue.observe(viewLifecycleOwner) { newTime ->
            timerText.text = formatTime(newTime)
        }

        return view
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}