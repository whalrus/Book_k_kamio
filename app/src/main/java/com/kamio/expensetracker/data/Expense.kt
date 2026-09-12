package com.kamio.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCents: Long,       // stored in cents to avoid float rounding issues
    val category: Category,
    val note: String,
    val dateEpochDay: Long,      // java.time.LocalDate.toEpochDay()
    val createdAtMillis: Long = System.currentTimeMillis()
)
