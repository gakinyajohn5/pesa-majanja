package com.pesamjanja.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pesamjanja.app.data.entity.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Insert
    suspend fun insert(debt: Debt): Long

    @Update
    suspend fun update(debt: Debt)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM debts WHERE direction = 'OWED_TO_ME' ORDER BY isSettled ASC, date DESC")
    fun getOwedToMe(): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE direction = 'I_OWE' ORDER BY isSettled ASC, date DESC")
    fun getIOwe(): Flow<List<Debt>>

    @Query("UPDATE debts SET isSettled = 1 WHERE id = :id")
    suspend fun markSettled(id: Long)
}
