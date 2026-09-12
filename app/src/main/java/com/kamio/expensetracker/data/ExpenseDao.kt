package com.kamio.expensetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay DESC, id DESC")
    fun observeForRange(startEpochDay: Long, endEpochDay: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM expenses WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay")
    fun observeTotalForRange(startEpochDay: Long, endEpochDay: Long): Flow<Long>

    @Query("SELECT category, COALESCE(SUM(amountCents), 0) as total FROM expenses WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay GROUP BY category")
    fun observeCategoryTotalsForRange(startEpochDay: Long, endEpochDay: Long): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM monthly_budget WHERE yearMonth = :yearMonth LIMIT 1")
    fun observeBudget(yearMonth: String): Flow<MonthlyBudget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBudget(budget: MonthlyBudget)
}

data class CategoryTotal(
    val category: Category,
    val total: Long
)
