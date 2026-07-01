package com.pesamjanja.app.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pesamjanja.app.ui.common.rememberPesaApp
import com.pesamjanja.app.ui.components.ConfirmTransactionCard
import com.pesamjanja.app.ui.dashboard.DashboardViewModel

@Composable
fun ReviewPendingScreen() {
    val app = rememberPesaApp()
    val viewModel: DashboardViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DashboardViewModel(app.budgetRepository, app.transactionRepository) }
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by app.categoryRepository.all.collectAsStateWithLifecycle(initialValue = emptyList())

    if (state.pendingTransactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nothing to review. We move.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.pendingTransactions, key = { it.id }) { txn ->
            ConfirmTransactionCard(
                transaction = txn,
                categories = categories,
                onConfirm = { t, cat -> viewModel.confirmTransaction(t, cat) },
                onDismiss = { viewModel.dismissTransaction(it) }
            )
        }
    }
}
