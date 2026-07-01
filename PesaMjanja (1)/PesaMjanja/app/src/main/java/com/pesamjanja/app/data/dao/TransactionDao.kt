package com.pesamjanja.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pesamjanja.app.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions WHERE isConfirmed = 1 ORDER BY timestamp DESC")
    fun getConfirmedTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isConfirmed = 0 ORDER BY timestamp DESC")
    fun getPendingTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isConfirmed = 1 AND category = :category ORDER BY timestamp DESC")
    fun getConfirmedByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isConfirmed = 1 AND category = :category AND timestamp BETWEEN :start AND :end")
    fun getSpentForCategoryInRange(category: String, start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE isConfirmed = 1 AND timestamp BETWEEN :start AND :end")
    fun getTotalSpentInRange(start: Long, end: Long): Flow<Double>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?
}
