package com.pesamjanja.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pesamjanja.app.data.entity.SplitBill
import com.pesamjanja.app.data.entity.SplitBillParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitBillDao {

    @Insert
    suspend fun insertBill(bill: SplitBill): Long

    @Insert
    suspend fun insertParticipants(participants: List<SplitBillParticipant>)

    @Update
    suspend fun updateParticipant(participant: SplitBillParticipant)

    @Query("DELETE FROM split_bills WHERE id = :id")
    suspend fun deleteBill(id: Long)

    @Query("SELECT * FROM split_bills ORDER BY createdAt DESC")
    fun getAllBills(): Flow<List<SplitBill>>

    @Query("SELECT * FROM split_bill_participants WHERE splitBillId = :billId")
    fun getParticipantsForBill(billId: Long): Flow<List<SplitBillParticipant>>
}
