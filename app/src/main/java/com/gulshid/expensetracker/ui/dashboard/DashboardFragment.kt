package com.gulshid.expensetracker.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentDashboardBinding
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.ui.expense.ExpenseListState
import com.gulshid.expensetracker.ui.expense.ExpenseViewModel
import com.gulshid.expensetracker.ui.history.ExpenseAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    // Shared ViewModel — same instance as HistoryFragment when scoped to Activity
    private val expenseViewModel: ExpenseViewModel by viewModels()

    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupGreeting()
        setupCategoryChips()
        setupRecyclerView()
        setupListeners()
        observeExpenses()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.tvGreeting.text = when {
            hour < 12 -> "Good morning,"
            hour < 17 -> "Good afternoon,"
            else      -> "Good evening,"
        }
        val name = firebaseAuth.currentUser?.displayName
        binding.tvUserName.text = if (!name.isNullOrBlank()) name else "there"
    }

    private fun setupCategoryChips() {
        val categories = listOf(
            "All" to "📊", "Food" to "🍔", "Transport" to "🚗",
            "Shopping" to "🛍️", "Health" to "💊", "Entertainment" to "🎬", "Other" to "📦"
        )
        categories.forEachIndexed { index, (label, emoji) ->
            val chip = Chip(requireContext()).apply {
                text            = "$emoji $label"
                isCheckable     = true
                isChecked       = index == 0
                chipStrokeWidth = 1f
                setChipBackgroundColorResource(android.R.color.transparent)
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onDeleteClick = { expense -> expenseViewModel.deleteExpense(expense.id) }
        )
        binding.rvRecentExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addExpenseFragment)
        }
        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }
    }

    private fun observeExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.expenseListState.collect { state ->
                    when (state) {
                        is ExpenseListState.Loading -> {
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is ExpenseListState.Success -> {
                            updateDashboard(state.expenses)
                        }
                        is ExpenseListState.Error -> {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun updateDashboard(expenses: List<Expense>) {
        // Update total amount card
        val total = expenses.sumOf { it.amount }
        binding.tvTotalAmount.text = "$${"%.2f".format(total)}"

        // Show only the 5 most recent on dashboard
        val recent = expenses.take(5)
        if (recent.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvRecentExpenses.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvRecentExpenses.visibility = View.VISIBLE
            expenseAdapter.submitList(recent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
