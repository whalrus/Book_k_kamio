package com.kamio.expensetracker.data

import androidx.room.Entity

/**
 * One row per calendar month (yearMonth = "2026-09").
 * incomeCents is the total household income expected that month, used to
 * compute the "left to save" figure alongside tracked expenses.
 */
@Entity(tableName = "monthly_budget", primaryKeys = ["yearMonth"])
data class MonthlyBudget(
    val yearMonth: String,
    val incomeCents: Long
)
