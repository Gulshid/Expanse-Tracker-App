package com.gulshid.expansetracker.ui.dashboard


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        binding.titleTv.text = "Dashboard Overview"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}