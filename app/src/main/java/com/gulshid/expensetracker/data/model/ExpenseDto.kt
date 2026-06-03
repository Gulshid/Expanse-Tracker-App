package com.gulshid.expensetracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.gulshid.expensetracker.domain.model.Expense

/**
 * Firestore-serializable representation of an expense.
 * All fields have defaults so Firestore can deserialize with no-arg constructor.
 * @DocumentId is automatically populated by Firestore with the document ID.
 */
data class ExpenseDto(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
) {
    // Convert Firestore DTO → Domain model
    fun toDomain(): Expense = Expense(
        id            = id,
        userId        = userId,
        amount        = amount,
        category      = category,
        description   = description,
        paymentMethod = paymentMethod,
        timestamp     = timestamp?.toDate()?.time ?: System.currentTimeMillis()
    )

    companion object {
        // Convert Domain model → Firestore map (exclude id — Firestore manages it)
        fun fromDomain(expense: Expense): Map<String, Any> = mapOf(
            "userId"        to expense.userId,
            "amount"        to expense.amount,
            "category"      to expense.category,
            "description"   to expense.description,
            "paymentMethod" to expense.paymentMethod,
            "timestamp"     to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
    }
}
