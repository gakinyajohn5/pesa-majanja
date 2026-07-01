package com.pesamjanja.app.data.repository

import com.pesamjanja.app.data.dao.BudgetPlanDao
import com.pesamjanja.app.data.entity.BudgetPlan
import com.pesamjanja.app.data.entity.CategorySplit
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetPlanDao) {
    val activePlan: Flow<BudgetPlan?> = dao.getActivePlan()

    fun splitsFor(planId: Long): Flow<List<CategorySplit>> = dao.getSplitsForPlan(planId)

    suspend fun savePlan(plan: BudgetPlan, splits: List<CategorySplit>): Long =
        dao.savePlanWithSplits(plan, splits)
}
