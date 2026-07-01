package com.pesamjanja.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pesamjanja.app.ui.common.rememberPesaApp
import com.pesamjanja.app.ui.components.AlertBanner
import com.pesamjanja.app.ui.components.CategoryBreakdownRow
import com.pesamjanja.app.ui.components.ConfirmTransactionCard

@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit,
    onOpenSplitBill: () -> Unit,
    onOpenDebtTracker: () -> Unit,
    onOpenHelbSetup: () -> Unit
) {
    val app = rememberPesaApp()
    val viewModel: DashboardViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DashboardViewModel(app.budgetRepository, app.transactionRepository) }
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by app.categoryRepository.all.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesa Mjanja") },
                actions = {
                    IconButton(onClick = onOpenDebtTracker) {
                        Icon(Icons.Filled.PeopleAlt, contentDescription = "Den / Debt Tracker")
                    }
                    IconButton(onClick = onOpenSplitBill) {
                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Split Bill")
                    }
                    IconButton(onClick = viewModel::toggleGhostMode) {
                        Icon(
                            imageVector = if (state.ghostMode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Ghost Mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense, containerColor = MaterialTheme.colorScheme.secondary) {
                Icon(Icons.Filled.Add, contentDescription = "Add expense", tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
    ) { padding ->
        if (!state.hasActivePlan) {
            NoPlanState(modifier = Modifier.padding(padding), onSetUp = onOpenHelbSetup)
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TopStatsCard(state)
            }

            items(state.alerts) { alert ->
                AlertBanner(alert)
            }

            if (state.pendingTransactions.isNotEmpty()) {
                item {
                    Text(
                        "Confirm these (${state.pendingTransactions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                items(state.pendingTransactions, key = { it.id }) { txn ->
                    ConfirmTransactionCard(
                        transaction = txn,
                        categories = categories,
                        onConfirm = { t, cat -> viewModel.confirmTransaction(t, cat) },
                        onDismiss = { viewModel.dismissTransaction(it) }
                    )
                }
            }

            item {
                Text("Where it's going", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(state.categories, key = { it.name }) { category ->
                CategoryBreakdownRow(progress = category, ghostMode = state.ghostMode)
            }

            item { Spacer(Modifier.height(64.dp)) } // room for FAB
        }
    }
}

@Composable
private fun TopStatsCard(state: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "${state.weeksRemaining} weeks left",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (state.ghostMode) "KES ••••" else "KES ${"%,.0f".format(state.amountLeft)} left",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (state.ghostMode) "•••• / day to spend" else "KES ${"%,.0f".format(state.dailySpendable)} / day to spend",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { state.percentSpent.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(state.percentSpent * 100).toInt()}% of KES ${"%,.0f".format(state.totalBudget)} spent",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun NoPlanState(modifier: Modifier = Modifier, onSetUp: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No active plan yet.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("Set up your HELB/upkeep split to see your dashboard.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSetUp) {
                Text("Set up budget")
            }
        }
    }
}
