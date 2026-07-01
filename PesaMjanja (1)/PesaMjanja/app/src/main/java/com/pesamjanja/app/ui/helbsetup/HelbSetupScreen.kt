package com.pesamjanja.app.ui.helbsetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pesamjanja.app.ui.common.rememberPesaApp

@Composable
fun HelbSetupScreen(onPlanLocked: () -> Unit) {
    val app = rememberPesaApp()
    val viewModel: HelbSetupViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HelbSetupViewModel(app.budgetRepository, app.transactionRepository) }
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val totalPercent = state.splits.sumOf { it.percent }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            text = "Set up your HELB / upkeep",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (state.detectedFromNotification)
                "We spotted a deposit — confirm it's your disbursement."
            else
                "Tell us what you're working with this semester.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.totalAmount,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Total amount (KES)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.weeks,
            onValueChange = viewModel::onWeeksChanged,
            label = { Text("Weeks until next disbursement") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Category split", fontWeight = FontWeight.SemiBold)
            Text(
                text = "$totalPercent%",
                color = if (totalPercent == 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.splits, key = { it.name }) { split ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(split.name)
                            Text("${split.percent}%", fontWeight = FontWeight.SemiBold)
                        }
                        Slider(
                            value = split.percent.toFloat(),
                            onValueChange = { viewModel.onSplitChanged(split.name, it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 19
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { viewModel.lockPlan(onPlanLocked) },
            enabled = state.totalAmount.toDoubleOrNull() != null &&
                state.weeks.toIntOrNull() != null && totalPercent == 100,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lock Plan", modifier = Modifier.padding(vertical = 6.dp))
        }
    }
}
