package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkBudgetDao {
    @Query("SELECT * FROM link_budgets ORDER BY timestamp DESC")
    fun getAllLinkBudgets(): Flow<List<LinkBudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinkBudget(linkBudget: LinkBudgetEntity)

    @Delete
    suspend fun deleteLinkBudget(linkBudget: LinkBudgetEntity)

    @Query("DELETE FROM link_budgets")
    suspend fun clearAll()
}
