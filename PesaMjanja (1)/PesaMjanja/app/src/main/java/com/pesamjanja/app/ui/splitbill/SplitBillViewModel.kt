package com.pesamjanja.app.ui.splitbill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesamjanja.app.data.entity.SplitBill
import com.pesamjanja.app.data.entity.SplitBillParticipant
import com.pesamjanja.app.data.repository.SplitBillRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SplitBillViewModel(private val repository: SplitBillRepository) : ViewModel() {

    val bills: StateFlow<List<SplitBill>> =
        repository.allBills.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun participantsFor(billId: Long) = repository.participantsFor(billId)

    fun createBill(title: String, totalAmount: Double, participantNames: List<String>, onDone: (Long) -> Unit) {
        if (participantNames.isEmpty() || totalAmount <= 0) return
        val perPerson = totalAmount / participantNames.size
        viewModelScope.launch {
            val billId = repository.createBill(
                bill = SplitBill(
                    title = title,
                    totalAmount = totalAmount,
                    numberOfPeople = participantNames.size,
                    perPersonAmount = perPerson,
                    createdAt = System.currentTimeMillis()
                ),
                participants = participantNames.map {
                    SplitBillParticipant(splitBillId = 0, name = it, amountOwed = perPerson)
                }
            )
            onDone(billId)
        }
    }

    fun setPaid(participant: SplitBillParticipant, paid: Boolean) {
        viewModelScope.launch { repository.setPaid(participant, paid) }
    }

    fun deleteBill(id: Long) {
        viewModelScope.launch { repository.deleteBill(id) }
    }
}
