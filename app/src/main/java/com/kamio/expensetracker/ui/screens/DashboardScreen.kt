package com.kamio.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kamio.expensetracker.ui.components.BarChart
import com.kamio.expensetracker.ui.components.BarEntry
import com.kamio.expensetracker.ui.components.PieChart
import com.kamio.expensetracker.ui.components.PieSlice
import com.kamio.expensetracker.ui.components.defaultSliceColors
import com.kamio.expensetracker.viewmodel.ExpenseViewModel
import java.time.format.TextStyle
import java.util.Locale

private fun formatEuros(cents: Long): String {
    val sign = if (cents < 0) "-" else ""
    val abs = kotlin.math.abs(cents)
    return "$sign€${abs / 100}.${(abs % 100).toString().padStart(2, '0')}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onAddExpense: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val trend by viewModel.sixMonthTrend.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kamio Ledger") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSelector(
                    monthLabel = state.month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + state.month.year,
                    onPrev = { viewModel.changeMonth(-1) },
                    onNext = { viewModel.changeMonth(1) }
                )
            }

            item {
                SummaryCard(
                    incomeCents = state.incomeCents,
                    spentCents = state.spentCents,
                    leftCents = state.leftToSaveCents,
                    onEditIncome = onOpenSettings
                )
            }

            if (state.categoryTotals.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 12.dp)) {
                            Text(
                                "Spending by category",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            val colors = defaultSliceColors(state.categoryTotals.size)
                            val slices = state.categoryTotals.mapIndexed { index, ct ->
                                PieSlice(ct.category.label, ct.total.toFloat(), colors[index])
                            }
                            PieChart(slices = slices, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Last 6 months", style = MaterialTheme.typography.titleMedium)
                        val barColor = MaterialTheme.colorScheme.primary
                        val entries = trend.map {
                            BarEntry(
                                label = it.month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                value = (it.totalCents / 100).toFloat(),
                                color = barColor
                            )
                        }
                        BarChart(entries = entries, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.TextButton(onClick = onOpenHistory) {
                        Text("View all expenses this month (${state.expenses.size})")
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(monthLabel: String, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
        }
        Text(monthLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun SummaryCard(
    incomeCents: Long,
    spentCents: Long,
    leftCents: Long,
    onEditIncome: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryStat(label = "Income", value = formatEuros(incomeCents))
                SummaryStat(label = "Tracked spend", value = formatEuros(spentCents))
                SummaryStat(
                    label = "Left to save",
                    value = formatEuros(leftCents),
                    valueColor = if (leftCents < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                androidx.compose.material3.TextButton(onClick = onEditIncome) {
                    Text("Set monthly income")
                }
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleMedium, color = valueColor)
    }
}
