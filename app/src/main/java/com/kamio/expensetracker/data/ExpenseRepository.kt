package com.kamio.expensetracker.data

import java.time.LocalDate
import java.time.YearMonth

class ExpenseRepository(private val dao: ExpenseDao) {

    fun observeExpensesForMonth(month: YearMonth) = dao.observeForRange(
        month.atDay(1).toEpochDay(),
        month.atEndOfMonth().toEpochDay()
    )

    fun observeTotalForMonth(month: YearMonth) = dao.observeTotalForRange(
        month.atDay(1).toEpochDay(),
        month.atEndOfMonth().toEpochDay()
    )

    fun observeCategoryTotalsForMonth(month: YearMonth) = dao.observeCategoryTotalsForRange(
        month.atDay(1).toEpochDay(),
        month.atEndOfMonth().toEpochDay()
    )

    fun observeAllExpenses() = dao.observeAll()

    fun observeBudget(month: YearMonth) = dao.observeBudget(month.toString())

    suspend fun setBudget(month: YearMonth, incomeCents: Long) {
        dao.setBudget(MonthlyBudget(month.toString(), incomeCents))
    }

    suspend fun addExpense(amountCents: Long, category: Category, note: String, date: LocalDate) {
        dao.insert(
            Expense(
                amountCents = amountCents,
                category = category,
                note = note,
                dateEpochDay = date.toEpochDay()
            )
        )
    }

    suspend fun updateExpense(expense: Expense) = dao.update(expense)

    suspend fun deleteExpense(expense: Expense) = dao.delete(expense)
}
