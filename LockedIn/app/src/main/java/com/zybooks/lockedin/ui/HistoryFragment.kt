package com.zybooks.lockedin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.zybooks.lockedin.R
import com.zybooks.lockedin.ui.adapter.HistoryAdapter
import com.zybooks.lockedin.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        val historyList = view.findViewById<RecyclerView>(R.id.history_list)
        viewModel.sessionHistory.observe(viewLifecycleOwner) { sessions ->
            historyList.adapter = HistoryAdapter(sessions)
        }

        return view
    }
}
