package com.pesamjanja.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "budget_plans")
data class BudgetPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalAmount: Double,
    val startDate: Long,
    val endDate: Long,
    val weeksCount: Int,
    val isActive: Boolean = true
)

/**
 * One row per category allocation within a BudgetPlan.
 * Kept as a related table (rather than an embedded map) so each split can be
 * queried/updated independently and joined against actual spend per category.
 */
@Entity(
    tableName = "category_splits",
    foreignKeys = [
        ForeignKey(
            entity = BudgetPlan::class,
            parentColumns = ["id"],
            childColumns = ["budgetPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CategorySplit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val budgetPlanId: Long,
    val categoryName: String,
    val allocatedAmount: Double,
    val allocatedPercent: Double
)
