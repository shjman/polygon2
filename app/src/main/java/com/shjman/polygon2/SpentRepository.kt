package com.shjman.polygon2

interface SpentRepository {
    suspend fun saveSpentAmount(spentAmount: Int)
}