package com.gulshid.expensetracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.gulshid.expensetracker.domain.model.Expense

data class ExpenseDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val paymentMethod: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
) {
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
        fun fromDomain(expense: Expense): Map<String, Any> = mapOf(
            "userId"        to expense.userId,
            "amount"        to expense.amount,
            "category"      to expense.category,
            "description"   to expense.description,
            "paymentMethod" to expense.paymentMethod,
            "timestamp"     to FieldValue.serverTimestamp()
        )
    }
}
