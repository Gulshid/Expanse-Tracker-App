package com.gulshid.expensetracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gulshid.expensetracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // This tells Hilt to provide dependencies to this Activity
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Test your binding by referencing a UI element safely without findViewById
        binding.welcomeTextView.text = "Welcome to Expense Tracker!"
    }
}