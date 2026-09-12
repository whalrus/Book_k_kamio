package com.kamio.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kamio.expensetracker.data.Expense
import com.kamio.expensetracker.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private fun formatEuros(cents: Long): String = "€${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val formatter = remember(java.util.Locale.getDefault()) { DateTimeFormatter.ofPattern("EEE d MMM") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("This month's expenses") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.expenses.isEmpty()) {
                item { Text("No expenses logged yet for this month.") }
            }
            items(state.expenses, key = { it.id }) { expense ->
                ExpenseRow(expense = expense, formatter = formatter, onDelete = { viewModel.deleteExpense(expense) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, formatter: DateTimeFormatter, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(expense.category.label, style = MaterialTheme.typography.bodyLarge)
            val dateLabel = LocalDate.ofEpochDay(expense.dateEpochDay).format(formatter)
            val subtitle = if (expense.note.isNotBlank()) "$dateLabel · ${expense.note}" else dateLabel
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(formatEuros(expense.amountCents), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
