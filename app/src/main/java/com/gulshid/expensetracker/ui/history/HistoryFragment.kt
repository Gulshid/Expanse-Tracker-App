package com.gulshid.expensetracker.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentHistoryBinding
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.ui.expense.ExpenseListState
import com.gulshid.expensetracker.ui.expense.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    // Full list kept locally so filter chips can work without a new Firestore call
    private var allExpenses: List<Expense> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)

        setupRecyclerView()
        setupFilterChips()
        observeExpenses()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onDeleteClick = { expense -> viewModel.deleteExpense(expense.id) }
        )
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, _ ->
            applyFilter()
        }
    }

    private fun observeExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.expenseListState.collect { state ->
                    when (state) {
                        is ExpenseListState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is ExpenseListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            allExpenses = state.expenses
                            applyFilter()
                        }
                        is ExpenseListState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val filtered = when (binding.chipGroupFilter.checkedChipId) {
            R.id.chipThisWeek    -> filterByDaysAgo(7)
            R.id.chipThisMonth   -> filterByDaysAgo(30)
            R.id.chipLast3Months -> filterByDaysAgo(90)
            else                 -> allExpenses   // chipAll or nothing
        }
        showList(filtered)
    }

    private fun filterByDaysAgo(days: Int): List<Expense> {
        val cutoff = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }.timeInMillis
        return allExpenses.filter { it.timestamp >= cutoff }
    }

    private fun showList(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvHistory.visibility    = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvHistory.visibility    = View.VISIBLE
            expenseAdapter.submitList(expenses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
