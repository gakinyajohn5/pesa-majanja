package com.pesamjanja.app

import android.app.Application
import com.pesamjanja.app.data.AppDatabase
import com.pesamjanja.app.data.repository.BudgetRepository
import com.pesamjanja.app.data.repository.CategoryRepository
import com.pesamjanja.app.data.repository.DebtRepository
import com.pesamjanja.app.data.repository.SplitBillRepository
import com.pesamjanja.app.data.repository.TransactionRepository

class PesaMjanjaApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val transactionRepository: TransactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val categoryRepository: CategoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val budgetRepository: BudgetRepository by lazy { BudgetRepository(database.budgetPlanDao()) }
    val splitBillRepository: SplitBillRepository by lazy { SplitBillRepository(database.splitBillDao()) }
    val debtRepository: DebtRepository by lazy { DebtRepository(database.debtDao()) }
}
