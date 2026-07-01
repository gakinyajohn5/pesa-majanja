package com.pesamjanja.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pesamjanja.app.data.entity.BudgetPlan
import com.pesamjanja.app.data.entity.CategorySplit
import kotlinx.coroutines.flow.Flow

data class BudgetPlanWithSplits(
    val plan: BudgetPlan,
    val splits: List<CategorySplit>
)

@Dao
interface BudgetPlanDao {

    @Insert
    suspend fun insertPlan(plan: BudgetPlan): Long

    @Insert
    suspend fun insertSplits(splits: List<CategorySplit>)

    @Update
    suspend fun updatePlan(plan: BudgetPlan)

    @Query("UPDATE budget_plans SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAllPlans()

    @Transaction
    suspend fun savePlanWithSplits(plan: BudgetPlan, splits: List<CategorySplit>): Long {
        deactivateAllPlans()
        val planId = insertPlan(plan)
        insertSplits(splits.map { it.copy(budgetPlanId = planId) })
        return planId
    }

    @Query("SELECT * FROM budget_plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<BudgetPlan?>

    @Query("SELECT * FROM category_splits WHERE budgetPlanId = :planId")
    fun getSplitsForPlan(planId: Long): Flow<List<CategorySplit>>
}
