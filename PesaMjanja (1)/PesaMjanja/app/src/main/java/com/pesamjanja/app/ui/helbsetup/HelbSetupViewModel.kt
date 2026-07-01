package com.pesamjanja.app.ui.helbsetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesamjanja.app.data.entity.BudgetPlan
import com.pesamjanja.app.data.entity.CategorySplit
import com.pesamjanja.app.data.repository.BudgetRepository
import com.pesamjanja.app.data.repository.TransactionRepository
import com.pesamjanja.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

data class CategorySliderState(val name: String, val percent: Int)

data class HelbSetupUiState(
    val totalAmount: String = "",
    val weeks: String = "13",
    val detectedFromNotification: Boolean = false,
    val splits: List<CategorySliderState> = DEFAULT_SPLITS,
    val planSaved: Boolean = false
) {
    companion object {
        val DEFAULT_SPLITS = listOf(
            CategorySliderState("Food", 40),
            CategorySliderState("Boda/Uber", 15),
            CategorySliderState("Bundles/Data", 10),
            CategorySliderState("Savings", 10),
            CategorySliderState("Fun/Hangouts", 10),
            CategorySliderState("Black Tax/Home", 10),
            CategorySliderState("Misc", 5),
        )
    }
}

class HelbSetupViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HelbSetupUiState())
    val uiState: StateFlow<HelbSetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val pending = transactionRepository.pendingTransactions.first()
            val received = pending.firstOrNull { it.type == TransactionType.RECEIVED }
            if (received != null) {
                _uiState.value = _uiState.value.copy(
                    totalAmount = received.amount.toString(),
                    detectedFromNotification = true
                )
            }
        }
    }

    fun onAmountChanged(value: String) {
        _uiState.value = _uiState.value.copy(totalAmount = value, detectedFromNotification = false)
    }

    fun onWeeksChanged(value: String) {
        _uiState.value = _uiState.value.copy(weeks = value)
    }

    fun onSplitChanged(name: String, percent: Int) {
        val updated = _uiState.value.splits.map {
            if (it.name == name) it.copy(percent = percent) else it
        }
        _uiState.value = _uiState.value.copy(splits = updated)
    }

    fun totalPercent(): Int = _uiState.value.splits.sumOf { it.percent }

    fun lockPlan(onDone: () -> Unit) {
        val state = _uiState.value
        val total = state.totalAmount.toDoubleOrNull() ?: return
        val weeks = state.weeks.toIntOrNull() ?: return
        if (total <= 0 || weeks <= 0) return

        val start = System.currentTimeMillis()
        val end = Calendar.getInstance().apply {
            timeInMillis = start
            add(Calendar.WEEK_OF_YEAR, weeks)
        }.timeInMillis

        val plan = BudgetPlan(
            totalAmount = total,
            startDate = start,
            endDate = end,
            weeksCount = weeks,
            isActive = true
        )

        val splits = state.splits.map {
            CategorySplit(
                budgetPlanId = 0, // overwritten by savePlanWithSplits
                categoryName = it.name,
                allocatedAmount = total * it.percent / 100.0,
                allocatedPercent = it.percent.toDouble()
            )
        }

        viewModelScope.launch {
            budgetRepository.savePlan(plan, splits)
            _uiState.value = _uiState.value.copy(planSaved = true)
            onDone()
        }
    }
}
