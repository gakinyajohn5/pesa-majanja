package com.pesamjanja.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesamjanja.app.data.entity.Transaction
import com.pesamjanja.app.data.repository.BudgetRepository
import com.pesamjanja.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class CategoryProgress(
    val name: String,
    val allocated: Double,
    val spent: Double
) {
    val percent: Float get() = if (allocated <= 0) 0f else (spent / allocated).toFloat().coerceAtLeast(0f)
    val isOverThreshold: Boolean get() = percent >= 0.8f
}

data class DashboardUiState(
    val hasActivePlan: Boolean = false,
    val weeksRemaining: Int = 0,
    val totalBudget: Double = 0.0,
    val spentTotal: Double = 0.0,
    val amountLeft: Double = 0.0,
    val dailySpendable: Double = 0.0,
    val percentSpent: Float = 0f,
    val categories: List<CategoryProgress> = emptyList(),
    val alerts: List<String> = emptyList(),
    val pendingTransactions: List<Transaction> = emptyList(),
    val ghostMode: Boolean = false
)

class DashboardViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val ghostModeFlow = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> =
        budgetRepository.activePlan.flatMapLatest { plan ->
            if (plan == null) {
                combine(
                    transactionRepository.pendingTransactions,
                    ghostModeFlow
                ) { pending, ghost ->
                    DashboardUiState(hasActivePlan = false, pendingTransactions = pending, ghostMode = ghost)
                }
            } else {
                combine(
                    budgetRepository.splitsFor(plan.id),
                    transactionRepository.confirmedTransactions,
                    transactionRepository.pendingTransactions,
                    ghostModeFlow
                ) { splits, confirmed, pending, ghost ->
                    val now = System.currentTimeMillis()
                    val msRemaining = (plan.endDate - now).coerceAtLeast(0)
                    val daysRemaining = TimeUnit.MILLISECONDS.toDays(msRemaining).toInt().coerceAtLeast(0)
                    val weeksRemaining = (daysRemaining / 7) + if (daysRemaining % 7 > 0) 1 else 0

                    val daysElapsed = (TimeUnit.MILLISECONDS.toDays(now - plan.startDate)).toInt().coerceAtLeast(0)
                    val totalDays = TimeUnit.MILLISECONDS.toDays(plan.endDate - plan.startDate).toInt().coerceAtLeast(1)
                    val isPastMidpoint = daysElapsed > totalDays / 2

                    val spentByCategory: Map<String, Double> = confirmed
                        .filter { it.category != null }
                        .groupBy { it.category!! }
                        .mapValues { (_, txns) -> txns.sumOf { it.amount } }

                    val categories = splits.map { split ->
                        CategoryProgress(
                            name = split.categoryName,
                            allocated = split.allocatedAmount,
                            spent = spentByCategory[split.categoryName] ?: 0.0
                        )
                    }

                    val alerts = categories
                        .filter { it.isOverThreshold && !isPastMidpoint }
                        .map { "${it.name} budget is ${(it.percent * 100).toInt()}% done and the period isn't even halfway." }

                    val spentTotal = confirmed.sumOf { it.amount }
                    val amountLeft = (plan.totalAmount - spentTotal).coerceAtLeast(0.0)
                    val dailySpendable = if (daysRemaining > 0) amountLeft / daysRemaining else amountLeft

                    DashboardUiState(
                        hasActivePlan = true,
                        weeksRemaining = weeksRemaining,
                        totalBudget = plan.totalAmount,
                        spentTotal = spentTotal,
                        amountLeft = amountLeft,
                        dailySpendable = dailySpendable,
                        percentSpent = if (plan.totalAmount > 0) (spentTotal / plan.totalAmount).toFloat() else 0f,
                        categories = categories,
                        alerts = alerts,
                        pendingTransactions = pending,
                        ghostMode = ghost
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun toggleGhostMode() {
        ghostModeFlow.value = !ghostModeFlow.value
    }

    fun confirmTransaction(transaction: Transaction, category: String) {
        viewModelScope.launch {
            transactionRepository.confirm(transaction, category)
        }
    }

    fun dismissTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction.id)
        }
    }
}
