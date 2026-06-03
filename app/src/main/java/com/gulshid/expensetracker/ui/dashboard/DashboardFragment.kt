package com.gulshid.expensetracker.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // FIX 3: Inject FirebaseAuth to read the logged-in user's display name
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupGreeting()
        setupCategoryChips()
        setupListeners()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.tvGreeting.text = when {
            hour < 12 -> "Good morning,"
            hour < 17 -> "Good afternoon,"
            else      -> "Good evening,"
        }

        // FIX 3: Read actual name from Firebase Auth current user
        // displayName is set during registration via UserProfileChangeRequest
        val name = firebaseAuth.currentUser?.displayName
        binding.tvUserName.text = if (!name.isNullOrBlank()) name else "there"
    }

    private fun setupCategoryChips() {
        val categories = listOf(
            "All"           to "📊",
            "Food"          to "🍔",
            "Transport"     to "🚗",
            "Shopping"      to "🛍️",
            "Health"        to "💊",
            "Entertainment" to "🎬",
            "Other"         to "📦"
        )
        categories.forEachIndexed { index, (label, emoji) ->
            val chip = Chip(requireContext()).apply {
                text           = "$emoji $label"
                isCheckable    = true
                isChecked      = index == 0
                chipStrokeWidth = 1f
                setChipBackgroundColorResource(android.R.color.transparent)
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupListeners() {
        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addExpenseFragment)
        }

        binding.tvSeeAll.setOnClickListener {
            // Full history navigation wired in Phase 5
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
