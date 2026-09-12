package com.kamio.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kamio.expensetracker.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ExpenseViewModel,
    onDone: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var incomeText by remember { mutableStateOf("") }

    LaunchedEffect(state.month) {
        incomeText = if (state.incomeCents > 0) (state.incomeCents / 100).toString() else ""
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Monthly income") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Set your household's expected income for ${state.month}. \"Left to save\" is calculated as income minus tracked expenses.")

            OutlinedTextField(
                value = incomeText,
                onValueChange = { input -> if (input.matches(Regex("^\\d*$"))) incomeText = input },
                label = { Text("Monthly income (€)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val euros = incomeText.toLongOrNull() ?: 0L
                    viewModel.setIncome(state.month, euros * 100)
                    onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
