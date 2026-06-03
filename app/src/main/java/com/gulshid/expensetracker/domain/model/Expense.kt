package com.gulshid.expensetracker.domain.model

data class Expense(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
