package com.shjman.polygon2

interface SpentRepository {
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun getAllSpending(): List<Spending>
    suspend fun getAllCategories(): List<Category>
}