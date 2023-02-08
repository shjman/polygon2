package com.shjman.polygon2.repository

import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.data.TrustedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDateTime

interface SpentRepository {
    suspend fun addTrustedUser(trustedUserEmail: String, onError: (errorText: String) -> Unit)
    fun checkIsUserSignIn(): Boolean
    suspend fun getCategories(onError: (errorText: String) -> Unit): List<Category>
    suspend fun getCategoriesFlow(onError: (errorText: String) -> Unit): Flow<List<Category>>
    fun getCurrentUserData(onError: (errorText: String) -> Unit): FirebaseUser?
    suspend fun getDocumentPath(onError: (errorText: String) -> Unit): String
    suspend fun getPopularCategory(onError: (errorText: String) -> Unit): Category
    suspend fun getSpending(
        localDateTime: LocalDateTime,
        onError: (errorText: String) -> Unit,
    ): Spending?

    suspend fun getSpendings(onError: (errorText: String) -> Unit): List<Spending>
    suspend fun getSpendingsFlow(onError: (errorText: String) -> Unit): Flow<List<Spending>>
    suspend fun getTrustedUsers(onError: (errorText: String) -> Unit): Flow<List<TrustedUser>>
    suspend fun isUserOwner(onError: (errorText: String) -> Unit): Flow<Boolean>
    suspend fun removeSharedDocumentPath(onError: (errorText: String) -> Unit)
    suspend fun removeSpending(
        onError: (errorText: String) -> Unit,
        uuid: String,
    )

    suspend fun saveCategory(
        category: Category,
        onError: (errorText: String) -> Unit,
    )

    suspend fun saveSpending(
        category: Category,
        note: String,
        onError: (errorText: String) -> Unit,
        spentAmount: Int,
    )

    suspend fun signOut()
    suspend fun updateDataAfterSuccessSignIn(onError: (errorText: String) -> Unit)
    suspend fun updatePopularCategoryID(
        onError: (errorText: String) -> Unit,
        popularCategoryID: String,
    )

    suspend fun updateSharedDocumentPath(documentPath: String)
    suspend fun updateSpending(
        onError: (errorText: String) -> Unit,
        spending: Spending,
        showSpendingUpdated: MutableSharedFlow<Unit>,
    )
}