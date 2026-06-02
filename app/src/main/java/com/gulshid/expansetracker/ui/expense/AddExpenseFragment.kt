package com.gulshid.expansetracker.ui.expense

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentAddExpenseBinding

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {
    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddExpenseBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}