package com.shjman.polygon2.repository

import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import java.time.LocalDateTime

interface SpentRepository {
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun getSpending(localDateTime: LocalDateTime): Spending?
    suspend fun getAllSpending(): List<Spending>
    suspend fun getAllCategories(): List<Category>
}