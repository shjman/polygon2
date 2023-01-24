package com.shjman.polygon2.repository

import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDateTime

interface SpentRepository {
    fun checkIsUserLoggedIn(): Boolean
    suspend fun getCategories(): List<Category>
    fun getCategoriesFlow(): Flow<List<Category>>
    suspend fun getPopularCategory(): Category
    suspend fun getSpending(localDateTime: LocalDateTime): Spending?
    suspend fun getSpendings(): List<Spending>
    fun getSpendingsFlow(): Flow<List<Spending>>
    suspend fun removeSpending(uuid: String)
    suspend fun saveCategory(category: Category)
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun updatePopularCategoryID(popularCategoryID: String)
    suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>)
}