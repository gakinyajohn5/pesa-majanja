package com.pesamjanja.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pesamjanja.app.domain.model.DebtDirection

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val amount: Double,
    val direction: DebtDirection,
    val note: String? = null,
    val date: Long,
    val isSettled: Boolean = false
)
