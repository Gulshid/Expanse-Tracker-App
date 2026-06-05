package com.gulshid.expensetracker.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentAnalyticsBinding
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.ui.expense.ExpenseListState
import com.gulshid.expensetracker.ui.expense.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnalyticsBinding.bind(view)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, options)
        }

        setupPieChart()
        observeExpenses()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled  = false
            isDrawHoleEnabled      = true
            holeRadius             = 52f
            transparentCircleRadius = 57f
            setHoleColor(Color.TRANSPARENT)
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            legend.isEnabled       = true
            isRotationEnabled      = true
            setExtraOffsets(16f, 16f, 16f, 16f)
        }
    }

    private fun observeExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.expenseListState.collect { state ->
                    when (state) {
                        is ExpenseListState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.pieChart.visibility    = View.GONE
                            binding.tvEmptyChart.visibility = View.GONE
                        }
                        is ExpenseListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            renderAnalytics(state.expenses)
                        }
                        is ExpenseListState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmptyChart.visibility = View.VISIBLE
                            binding.tvEmptyChart.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun renderAnalytics(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.pieChart.visibility    = View.GONE
            binding.tvEmptyChart.visibility = View.VISIBLE
            binding.tvTotalAnalytics.text  = "Total: $0.00"
            return
        }

        val totals = viewModel.categoryTotals(expenses)
        val total  = expenses.sumOf { it.amount }
        binding.tvTotalAnalytics.text = "Total: $${"%.2f".format(total)}"

        val entries = totals.map { (cat, amount) ->
            PieEntry(amount.toFloat(), cat)
        }

        val colors = listOf(
            Color.parseColor("#7C4DFF"), // Food - deep purple
            Color.parseColor("#00BCD4"), // Transport - cyan
            Color.parseColor("#FF6D00"), // Shopping - orange
            Color.parseColor("#43A047"), // Health - green
            Color.parseColor("#E91E63"), // Entertainment - pink
            Color.parseColor("#78909C")  // Other - grey
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            valueTextColor = Color.WHITE
            valueTextSize  = 12f
            sliceSpace     = 3f
            selectionShift = 6f
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))
        }

        binding.pieChart.apply {
            data = pieData
            visibility = View.VISIBLE
            invalidate()
            animateY(800)
        }
        binding.tvEmptyChart.visibility = View.GONE

        // Populate summary rows
        renderCategoryRows(totals, total)
    }

    private fun renderCategoryRows(totals: Map<String, Double>, grandTotal: Double) {
        val rows = listOf(
            binding.rowFood, binding.rowTransport, binding.rowShopping,
            binding.rowHealth, binding.rowEntertainment, binding.rowOther
        )
        val categories = listOf("Food", "Transport", "Shopping", "Health", "Entertainment", "Other")
        val emojis     = listOf("🍔", "🚗", "🛍️", "💊", "🎬", "📦")

        rows.forEachIndexed { index, row ->
            val cat    = categories[index]
            val amount = totals[cat] ?: 0.0
            val pct    = if (grandTotal > 0) (amount / grandTotal * 100).toInt() else 0

            row.tvCategoryName.text   = "${emojis[index]} $cat"
            row.tvCategoryAmount.text = "$${"%.2f".format(amount)}"
            row.tvCategoryPercent.text = "$pct%"
            row.progressCategory.progress = pct
            row.root.visibility = if (amount > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
