package com.pesamjanja.app.data.repository

import com.pesamjanja.app.data.dao.DebtDao
import com.pesamjanja.app.data.entity.Debt
import kotlinx.coroutines.flow.Flow

class DebtRepository(private val dao: DebtDao) {
    val owedToMe: Flow<List<Debt>> = dao.getOwedToMe()
    val iOwe: Flow<List<Debt>> = dao.getIOwe()

    suspend fun add(debt: Debt): Long = dao.insert(debt)
    suspend fun markSettled(id: Long) = dao.markSettled(id)
    suspend fun delete(id: Long) = dao.deleteById(id)
}
