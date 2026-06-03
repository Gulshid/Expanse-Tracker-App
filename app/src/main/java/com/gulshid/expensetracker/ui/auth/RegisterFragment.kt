package com.gulshid.expensetracker.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.registerUser(
                displayName = binding.etDisplayName.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                password = binding.etPassword.text.toString().trim(),
                confirmPassword = binding.etConfirmPassword.text.toString().trim()
            )
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state -> handleUiState(state) }
            }
        }
    }

    private fun handleUiState(state: AuthState) {
        when (state) {
            is AuthState.Idle -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
            }
            is AuthState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            }
            is AuthState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                viewModel.resetState()
                findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
            }
            is AuthState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
