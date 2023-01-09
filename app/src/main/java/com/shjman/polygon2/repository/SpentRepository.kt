package com.shjman.polygon2.repository

import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDateTime

interface SpentRepository {
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>)
    suspend fun removeSpending(uuid: String)
    fun getSpendingsFlow(): Flow<List<Spending>>
    suspend fun getSpendings(): List<Spending>
    suspend fun getSpending(localDateTime: LocalDateTime): Spending?
    suspend fun getAllCategories(): List<Category>
    fun getCategoriesFlow(): Flow<List<Category>>
    suspend fun saveCategory(category: Category)
    suspend fun getPopularCategory(): Category
    suspend fun updatePopularCategoryID(popularCategoryID: String)
}