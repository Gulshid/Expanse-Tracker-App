package com.gulshid.expensetracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gulshid.expensetracker.databinding.ItemExpenseCardBinding
import com.gulshid.expensetracker.domain.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val onDeleteClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvDescription.text =
                expense.description.ifBlank { expense.category }

            binding.tvAmount.text = "-${"%.2f".format(expense.amount)}"

            binding.tvCategoryAndDate.text = buildString {
                append(expense.category)
                append(" · ")
                append(formatDate(expense.timestamp))
            }

            binding.tvCategoryEmoji.text = categoryEmoji(expense.category)
            binding.tvPaymentMethod.text  = expense.paymentMethod

            // Long-press to delete
            binding.root.setOnLongClickListener {
                onDeleteClick(expense)
                true
            }
        }

        private fun formatDate(millis: Long): String =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))

        private fun categoryEmoji(category: String): String = when (category) {
            "Food"          -> "🍔"
            "Transport"     -> "🚗"
            "Shopping"      -> "🛍️"
            "Health"        -> "💊"
            "Entertainment" -> "🎬"
            else            -> "📦"
        }
    }
}
