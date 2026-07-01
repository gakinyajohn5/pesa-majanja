package com.pesamjanja.app.ui.debttracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesamjanja.app.data.entity.Debt
import com.pesamjanja.app.data.repository.DebtRepository
import com.pesamjanja.app.domain.model.DebtDirection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DebtTrackerViewModel(private val repository: DebtRepository) : ViewModel() {

    val owedToMe: StateFlow<List<Debt>> =
        repository.owedToMe.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val iOwe: StateFlow<List<Debt>> =
        repository.iOwe.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addDebt(personName: String, amount: Double, direction: DebtDirection, note: String?) {
        if (personName.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.add(
                Debt(
                    personName = personName,
                    amount = amount,
                    direction = direction,
                    note = note?.takeIf { it.isNotBlank() },
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun markSettled(id: Long) {
        viewModelScope.launch { repository.markSettled(id) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.delete(id) }
    }
}
