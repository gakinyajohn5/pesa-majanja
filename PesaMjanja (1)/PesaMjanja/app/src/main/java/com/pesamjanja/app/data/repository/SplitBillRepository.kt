package com.pesamjanja.app.data.repository

import com.pesamjanja.app.data.dao.SplitBillDao
import com.pesamjanja.app.data.entity.SplitBill
import com.pesamjanja.app.data.entity.SplitBillParticipant
import kotlinx.coroutines.flow.Flow

class SplitBillRepository(private val dao: SplitBillDao) {
    val allBills: Flow<List<SplitBill>> = dao.getAllBills()

    fun participantsFor(billId: Long): Flow<List<SplitBillParticipant>> =
        dao.getParticipantsForBill(billId)

    suspend fun createBill(bill: SplitBill, participants: List<SplitBillParticipant>): Long {
        val billId = dao.insertBill(bill)
        dao.insertParticipants(participants.map { it.copy(splitBillId = billId) })
        return billId
    }

    suspend fun setPaid(participant: SplitBillParticipant, paid: Boolean) =
        dao.updateParticipant(participant.copy(hasPaid = paid))

    suspend fun deleteBill(id: Long) = dao.deleteBill(id)
}
