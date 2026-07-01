package com.pesamjanja.app.ui.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesamjanja.app.data.entity.Transaction
import com.pesamjanja.app.data.repository.TransactionRepository
import com.pesamjanja.app.domain.model.TransactionSource
import com.pesamjanja.app.domain.model.TransactionType
import kotlinx.coroutines.launch

class AddExpenseViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    fun save(amount: Double, category: String, note: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            transactionRepository.insert(
                Transaction(
                    amount = amount,
                    type = TransactionType.SENT,
                    category = category,
                    counterparty = note?.takeIf { it.isNotBlank() },
                    source = TransactionSource.MANUAL,
                    timestamp = System.currentTimeMillis(),
                    isConfirmed = true // manual entries are confirmed immediately, per brief (same data path as confirmed)
                )
            )
            onDone()
        }
    }
}
