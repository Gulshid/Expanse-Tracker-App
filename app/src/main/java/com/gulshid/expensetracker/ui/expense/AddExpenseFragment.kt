package com.gulshid.expensetracker.ui.expense

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentAddExpenseBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddExpenseBinding.bind(view)

        // Pre-fill today's date
        binding.etDate.setText(formatDate(selectedDateMillis))

        setupListeners()
    }

    private fun setupListeners() {
        // Date picker via MaterialDatePicker
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnSaveExpense.setOnClickListener {
            if (validateInputs()) {
                // ViewModel call will be wired in Phase 4
                Toast.makeText(requireContext(), "Expense saved! (Phase 4 will persist to Firestore)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(selectedDateMillis)
            .build()

        picker.addOnPositiveButtonClickListener { millis ->
            selectedDateMillis = millis
            binding.etDate.setText(formatDate(millis))
        }

        picker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun validateInputs(): Boolean {
        val amount = binding.etAmount.text.toString().trim()

        if (amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0.0) {
            binding.tilAmount.error = "Please enter a valid amount"
            return false
        }
        binding.tilAmount.error = null

        if (binding.chipGroupCategory.checkedChipId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getSelectedCategory(): String {
        return when (binding.chipGroupCategory.checkedChipId) {
            R.id.chipFood          -> "Food"
            R.id.chipTransport     -> "Transport"
            R.id.chipShopping      -> "Shopping"
            R.id.chipHealth        -> "Health"
            R.id.chipEntertainment -> "Entertainment"
            else                   -> "Other"
        }
    }

    private fun getSelectedPaymentMethod(): String {
        return when (binding.chipGroupPayment.checkedChipId) {
            R.id.chipCash   -> "Cash"
            R.id.chipCard   -> "Card"
            R.id.chipOnline -> "Online"
            else            -> "Cash"
        }
    }

    private fun formatDate(millis: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
