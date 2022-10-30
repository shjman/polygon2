package com.shjman.polygon2.repository

import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDateTime

interface SpentRepository {
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>)
    suspend fun removeSpending(uuid:String)
    suspend fun getSpending(localDateTime: LocalDateTime): Spending?
    fun getAllSpendingFlow(): Flow<List<Spending>>
    suspend fun getAllCategories(): List<Category>
}