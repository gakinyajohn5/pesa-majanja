package com.pesamjanja.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pesamjanja.app.data.converter.Converters
import com.pesamjanja.app.data.dao.BudgetPlanDao
import com.pesamjanja.app.data.dao.CategoryDao
import com.pesamjanja.app.data.dao.DebtDao
import com.pesamjanja.app.data.dao.SplitBillDao
import com.pesamjanja.app.data.dao.TransactionDao
import com.pesamjanja.app.data.entity.BudgetPlan
import com.pesamjanja.app.data.entity.Category
import com.pesamjanja.app.data.entity.CategorySplit
import com.pesamjanja.app.data.entity.Debt
import com.pesamjanja.app.data.entity.DefaultCategories
import com.pesamjanja.app.data.entity.SplitBill
import com.pesamjanja.app.data.entity.SplitBillParticipant
import com.pesamjanja.app.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Transaction::class,
        Category::class,
        BudgetPlan::class,
        CategorySplit::class,
        SplitBill::class,
        SplitBillParticipant::class,
        Debt::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetPlanDao(): BudgetPlanDao
    abstract fun splitBillDao(): SplitBillDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pesa_mjanja.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default categories on first install.
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.categoryDao()?.insertAll(DefaultCategories.seed)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
