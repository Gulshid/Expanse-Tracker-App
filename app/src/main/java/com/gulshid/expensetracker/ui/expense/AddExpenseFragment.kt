package com.gulshid.expensetracker.ui.expense

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentAddExpenseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels()

    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddExpenseBinding.bind(view)

        binding.etDate.setText(formatDate(selectedDateMillis))

        setupListeners()
        observeActionState()
    }

    private fun setupListeners() {
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnSaveExpense.setOnClickListener {
            if (validateInputs()) {
                viewModel.saveExpense(
                    amount        = binding.etAmount.text.toString().trim().toDouble(),
                    category      = getSelectedCategory(),
                    description   = binding.etDescription.text.toString().trim(),
                    paymentMethod = getSelectedPaymentMethod(),
                    timestampMillis = selectedDateMillis
                )
            }
        }
    }

    private fun observeActionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionState.collect { state ->
                    when (state) {
                        is ExpenseState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveExpense.isEnabled = true
                        }
                        is ExpenseState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnSaveExpense.isEnabled = false
                        }
                        is ExpenseState.SaveSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveExpense.isEnabled = true
                            Toast.makeText(requireContext(), "Expense saved!", Toast.LENGTH_SHORT).show()
                            viewModel.resetActionState()
                            findNavController().popBackStack()
                        }
                        is ExpenseState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveExpense.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetActionState()
                        }
                        else -> Unit
                    }
                }
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
        val amountStr = binding.etAmount.text.toString().trim()
        if (amountStr.isEmpty() || amountStr.toDoubleOrNull() == null || amountStr.toDouble() <= 0.0) {
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

    private fun getSelectedCategory(): String = when (binding.chipGroupCategory.checkedChipId) {
        R.id.chipFood          -> "Food"
        R.id.chipTransport     -> "Transport"
        R.id.chipShopping      -> "Shopping"
        R.id.chipHealth        -> "Health"
        R.id.chipEntertainment -> "Entertainment"
        else                   -> "Other"
    }

    private fun getSelectedPaymentMethod(): String = when (binding.chipGroupPayment.checkedChipId) {
        R.id.chipCash   -> "Cash"
        R.id.chipCard   -> "Card"
        R.id.chipOnline -> "Online"
        else            -> "Cash"
    }

    private fun formatDate(millis: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
