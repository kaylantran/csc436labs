package com.zybooks.lockedin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zybooks.lockedin.R
import com.zybooks.lockedin.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val studyDuration = view.findViewById<EditText>(R.id.study_duration)
        val breakDuration = view.findViewById<EditText>(R.id.break_duration)

        val saveButton = view.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            viewModel.saveSettings(requireContext())
        }

        return view
    }
}
