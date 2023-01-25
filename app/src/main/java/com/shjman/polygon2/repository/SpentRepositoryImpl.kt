package com.shjman.polygon2.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SpentRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : SpentRepository {

    companion object {
        private const val COLLECTION_ENTRY_POINT = "entry_point"
        private const val COLLECTION_CATEGORIES = "categories"
        private const val COLLECTION_SPENDINGS = "spendings"
        private const val COLLECTION_TRUSTED_EMAILS = "trusted_emails"
        private val POPULAR_CATEGORY_ID = stringPreferencesKey("POPULAR_CATEGORY_ID")
    }

    override suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category) {
        val newData = mutableMapOf<String, Any>()
        val formatter = DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER)
        val currentLocalDateTime = LocalDateTime.now()
        val currentDateTimeString = currentLocalDateTime.format(formatter)
        val uuid = currentDateTimeString + UUID.randomUUID()
        newData["uuid"] = uuid
        newData["date"] = currentDateTimeString
        newData["spentAmount"] = spentAmount
        newData["categoryID"] = category.id
//        newData["currency"] = "zl"
        newData["note"] = note
        Timber.d("newData == $newData")

        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .document(uuid)
            .set(newData)
            .await()
    }

    override suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>) {
        val newData = mutableMapOf<String, Any>()
        newData["uuid"] = spending.uuid
        newData["date"] = spending.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
        newData["spentAmount"] = spending.spentAmount
        newData["categoryID"] = spending.category.id
        newData["note"] = spending.note

        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .document(spending.uuid)
            .set(newData)
        showSpendingUpdated.emit(Unit)
    }

    override suspend fun removeSpending(uuid: String) {
        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .document(uuid)
            .delete()
    }

    override suspend fun getSpending(localDateTime: LocalDateTime): Spending? {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .firstOrNull { localDateTime.isEqual(convertDateStringToLocalDateTime(it.date)) }
            ?.toSpending(getCategories())
    }

    override suspend fun getSpendings(): List<Spending> {
        val categories = getCategories()
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .map { it.toSpending(categories) }
    }

    override fun getSpendingsFlow(): Flow<List<Spending>> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_SPENDINGS)
            .snapshots()
            .map { it.toObjects(SpendingRemote::class.java) }
            .map { it to getCategories() }
            .map { (spendingRemote, categories) -> spendingRemote.map { it.toSpending(categories) } }
    }

    override suspend fun getCategories(): List<Category> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_CATEGORIES)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(CategoryRemote::class.java) }
            .map { it.toCategory() }
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_CATEGORIES)
            .snapshots()
            .map { it.toObjects(CategoryRemote::class.java) }
            .map { it.map { categoryRemote -> categoryRemote.toCategory() } }
    }

    override suspend fun saveCategory(category: Category) {
        val newData = mutableMapOf<String, String>()
        val id = category.id
        newData["id"] = id
        newData["name"] = category.name

        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_CATEGORIES)
            .document(id)
            .set(newData)
            .await()
    }

    override suspend fun updatePopularCategoryID(popularCategoryID: String) {
        dataStore.edit { preferences ->
            preferences[POPULAR_CATEGORY_ID] = popularCategoryID
        }
    }

    override suspend fun getPopularCategory(): Category {
        val popularCategoryID = dataStore.data
            .catch { Timber.e("error dataStore.data get POPULAR_CATEGORY_ID == ${it.message}") }
            .first()[POPULAR_CATEGORY_ID]
        return getCategories().firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }

    override fun checkIsUserLoggedIn() = firebaseAuth.currentUser != null

    override fun getUserData() = firebaseAuth.currentUser

    override suspend fun getTrustedUsers(): Flow<List<TrustedUser>?> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getUserEmail())
            .collection(COLLECTION_TRUSTED_EMAILS)
            .snapshots()
            .map { it.toObjects(TrustedUser::class.java) }
    }

    private fun getUserEmail(): String {
        return if (BuildConfig.mainCollectionPath == "testing_family") {
//            firebaseAuth.currentUser.uid    todo check this option . change it in the release
            val path = firebaseAuth.currentUser?.email   // todo change it if it's a shared collection
            path ?: throw Exception("wtf?? firebaseAuth.currentUser == null")
        } else {
            BuildConfig.mainCollectionPath
        }
    }
}
