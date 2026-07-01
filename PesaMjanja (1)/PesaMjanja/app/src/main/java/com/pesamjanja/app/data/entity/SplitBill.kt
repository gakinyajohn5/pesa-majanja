package com.pesamjanja.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "split_bills")
data class SplitBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val totalAmount: Double,
    val numberOfPeople: Int,
    val perPersonAmount: Double,
    val createdAt: Long
)

@Entity(
    tableName = "split_bill_participants",
    foreignKeys = [
        ForeignKey(
            entity = SplitBill::class,
            parentColumns = ["id"],
            childColumns = ["splitBillId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SplitBillParticipant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val splitBillId: Long,
    val name: String,
    /** Defaults to the bill's even split; can be manually overridden. */
    val amountOwed: Double,
    val hasPaid: Boolean = false,
    val phoneNumber: String? = null
)
