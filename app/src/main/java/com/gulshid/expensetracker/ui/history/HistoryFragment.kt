package com.gulshid.expensetracker.ui.history

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentHistoryBinding
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.ui.expense.ExpenseFilter
import com.gulshid.expensetracker.ui.expense.ExpenseListState
import com.gulshid.expensetracker.ui.expense.ExpenseState
import com.gulshid.expensetracker.ui.expense.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter
    private var allExpenses: List<Expense> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)

        setupRecyclerView()
        setupFilterChips()
        observeExpenses()
        observeActionState()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(onDeleteClick = { expense ->
            showDeleteDialog(expense)
        })
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }

    private fun showDeleteDialog(expense: Expense) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_delete_expense, null)

        val dialog = android.app.Dialog(requireContext(), R.style.DeleteDialogStyle)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val name = expense.description.ifBlank { expense.category }
        dialogView.findViewById<TextView>(R.id.tvExpenseName).text = name
        dialogView.findViewById<TextView>(R.id.tvExpenseCategory).text = expense.category
        dialogView.findViewById<TextView>(R.id.tvExpenseAmount).text =
            "-${"$"}${"%.2f".format(expense.amount)}"

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            viewModel.deleteExpense(expense.id)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipThisWeek    -> ExpenseFilter.THIS_WEEK
                R.id.chipThisMonth   -> ExpenseFilter.THIS_MONTH
                R.id.chipLast3Months -> ExpenseFilter.LAST_3_MONTHS
                else                 -> ExpenseFilter.ALL
            }
            viewModel.setFilter(filter)
            showList(viewModel.filteredExpenses(allExpenses))
        }
    }

    private fun observeExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.expenseListState.collect { state ->
                    when (state) {
                        is ExpenseListState.Loading -> {
                            binding.progressBar.visibility      = View.VISIBLE
                            binding.layoutEmptyState.visibility = View.GONE
                            binding.rvHistory.visibility        = View.GONE
                        }
                        is ExpenseListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            allExpenses = state.expenses
                            showList(viewModel.filteredExpenses(allExpenses))
                        }
                        is ExpenseListState.Error -> {
                            binding.progressBar.visibility      = View.GONE
                            binding.layoutEmptyState.visibility = View.VISIBLE
                            binding.rvHistory.visibility        = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun observeActionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionState.collect { state ->
                    when (state) {
                        is ExpenseState.DeleteSuccess -> {
                            Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show()
                            viewModel.resetActionState()
                        }
                        is ExpenseState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetActionState()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showList(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvHistory.visibility        = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvHistory.visibility        = View.VISIBLE
            expenseAdapter.submitList(expenses)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}