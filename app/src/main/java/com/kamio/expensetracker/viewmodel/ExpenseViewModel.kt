package com.kamio.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kamio.expensetracker.data.AppDatabase
import com.kamio.expensetracker.data.Category
import com.kamio.expensetracker.data.CategoryTotal
import com.kamio.expensetracker.data.Expense
import com.kamio.expensetracker.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class MonthTotal(val month: YearMonth, val totalCents: Long)

data class DashboardState(
    val month: YearMonth = YearMonth.now(),
    val incomeCents: Long = 0L,
    val spentCents: Long = 0L,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val expenses: List<Expense> = emptyList()
) {
    val leftToSaveCents: Long get() = incomeCents - spentCents
}

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(
        AppDatabase.getInstance(application).expenseDao()
    )

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<DashboardState> = selectedMonth.flatMapLatest { month ->
        combine(
            repository.observeBudget(month),
            repository.observeTotalForMonth(month),
            repository.observeCategoryTotalsForMonth(month),
            repository.observeExpensesForMonth(month)
        ) { budget, spent, categoryTotals, expenses ->
            DashboardState(
                month = month,
                incomeCents = budget?.incomeCents ?: 0L,
                spentCents = spent,
                categoryTotals = categoryTotals,
                expenses = expenses
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    /** Total spend for each of the last 6 months (oldest first), for the trend chart. */
    val sixMonthTrend: StateFlow<List<MonthTotal>> = repository.observeAllExpenses()
        .map { expenses ->
            val now = YearMonth.now()
            (5 downTo 0).map { offset ->
                val month = now.minusMonths(offset.toLong())
                val total = expenses
                    .filter {
                        val d = LocalDate.ofEpochDay(it.dateEpochDay)
                        YearMonth.from(d) == month
                    }
                    .sumOf { it.amountCents }
                MonthTotal(month, total)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun changeMonth(delta: Long) {
        selectedMonth.value = selectedMonth.value.plusMonths(delta)
    }

    fun setIncome(month: YearMonth, incomeCents: Long) {
        viewModelScope.launch { repository.setBudget(month, incomeCents) }
    }

    fun addExpense(amountCents: Long, category: Category, note: String, date: LocalDate) {
        viewModelScope.launch { repository.addExpense(amountCents, category, note, date) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }
}
