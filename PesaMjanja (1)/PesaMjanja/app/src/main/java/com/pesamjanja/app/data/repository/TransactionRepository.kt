package com.pesamjanja.app.data.repository

import com.pesamjanja.app.data.dao.TransactionDao
import com.pesamjanja.app.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val confirmedTransactions: Flow<List<Transaction>> = dao.getConfirmedTransactions()
    val pendingTransactions: Flow<List<Transaction>> = dao.getPendingTransactions()

    suspend fun insert(transaction: Transaction): Long = dao.insert(transaction)
    suspend fun update(transaction: Transaction) = dao.update(transaction)
    suspend fun delete(id: Long) = dao.deleteById(id)
    suspend fun confirm(transaction: Transaction, category: String) =
        dao.update(transaction.copy(category = category, isConfirmed = true))

    fun spentForCategory(category: String, start: Long, end: Long): Flow<Double> =
        dao.getSpentForCategoryInRange(category, start, end)

    fun totalSpent(start: Long, end: Long): Flow<Double> =
        dao.getTotalSpentInRange(start, end)
}
