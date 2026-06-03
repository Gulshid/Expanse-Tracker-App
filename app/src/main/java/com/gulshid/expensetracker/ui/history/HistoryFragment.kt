package com.gulshid.expensetracker.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        setupSwipeToDelete()
        setupFilterChips()
        observeExpenses()
        observeActionState()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(onDeleteClick = { viewModel.deleteExpense(it.id) })
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }

    // Swipe left to delete
    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val background = ColorDrawable(Color.parseColor("#FFE5E5"))
            private val paint = Paint().apply { color = Color.parseColor("#D32F2F"); textSize = 40f; isAntiAlias = true }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val expense = expenseAdapter.getItemAt(viewHolder.adapterPosition)
                viewModel.deleteExpense(expense.id)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                background.setBounds(
                    itemView.right + dX.toInt(), itemView.top,
                    itemView.right, itemView.bottom
                )
                background.draw(c)
                val deleteText = "🗑️ Delete"
                val textX = itemView.right - 220f
                val textY = itemView.top + (itemView.height / 2f) + 15f
                c.drawText(deleteText, textX, textY, paint)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvHistory)
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
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is ExpenseListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            allExpenses = state.expenses
                            showList(viewModel.filteredExpenses(allExpenses))
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
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvHistory.visibility    = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvHistory.visibility    = View.VISIBLE
            expenseAdapter.submitList(expenses)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
