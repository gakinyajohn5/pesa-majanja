package com.pesamjanja.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pesamjanja.app.domain.model.TransactionSource
import com.pesamjanja.app.domain.model.TransactionType

/**
 * A single money movement — either confirmed manually or auto-detected from an
 * M-Pesa notification (in which case it starts with isConfirmed = false).
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    /** Free-text category name; resolves against the Category table. Null until categorized. */
    val category: String?,
    /** Sender/recipient name parsed from the notification, if available. */
    val counterparty: String?,
    val source: TransactionSource,
    val timestamp: Long,
    val isConfirmed: Boolean = false,
    /** Raw notification text kept around so failed/odd parses can be debugged and tuned later. */
    val rawNotificationText: String? = null
)
