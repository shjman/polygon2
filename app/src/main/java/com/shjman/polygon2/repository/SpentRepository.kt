package com.shjman.polygon2.repository

import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.data.TrustedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDateTime

interface SpentRepository {
    suspend fun addTrustedUser(trustedUserEmail: String)
    fun checkIsUserSignIn(): Boolean
    suspend fun getCategories(): List<Category>
    suspend fun getCategoriesFlow(): Flow<List<Category>>
    fun getCurrentUserData(): FirebaseUser
    suspend fun getDocumentPath(): String
    suspend fun getPopularCategory(): Category
    suspend fun getSpending(localDateTime: LocalDateTime): Spending?
    suspend fun getSpendings(): List<Spending>
    suspend fun getSpendingsFlow(): Flow<List<Spending>>
    suspend fun getTrustedUsers(): Flow<List<TrustedUser>>
    suspend fun isUserOwner(): Flow<Boolean>
    suspend fun removeSharedDocumentPath()
    suspend fun removeSpending(uuid: String)
    suspend fun saveCategory(category: Category)
    suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category)
    suspend fun signOut()
    suspend fun updateDataAfterSuccessSignIn()
    suspend fun updatePopularCategoryID(popularCategoryID: String)
    suspend fun updateSharedDocumentPath(documentPath: String)
    suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>)
}