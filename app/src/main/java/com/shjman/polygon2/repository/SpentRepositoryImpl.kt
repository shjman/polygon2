package com.shjman.polygon2.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class SpentRepositoryImpl(
    private val fireStore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>,
) : SpentRepository {

    companion object {
        const val mainCollectionPath = BuildConfig.mainCollectionPath
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
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .document(uuid)
            .set(newData)
            .await()
    }

    override suspend fun updateSpending(spending: Spending, showSpendingUpdated: MutableSharedFlow<Unit>) {
        val newData = mutableMapOf<String, Any>()
        newData["uuid"] = spending.uuid
        newData["date"] = spending.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
        newData["spentAmount"] = spending.spentAmount
        newData["category"] = spending.category.toString()
        newData["note"] = spending.note

        fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .document(spending.uuid)
            .set(newData)
        showSpendingUpdated.emit(Unit)
    }

    override suspend fun removeSpending(uuid: String) {
        fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .document(uuid)
            .delete()
    }

    override suspend fun getSpending(localDateTime: LocalDateTime): Spending? {
        return fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .firstOrNull { localDateTime.isEqual(convertDateStringToLocalDateTime(it.date)) }
            ?.toSpending(getCategories())
    }

    override suspend fun getSpendings(): List<Spending> {
        return fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .map { it to getCategories() }
            .map { (spendingRemote, categories) -> spendingRemote.toSpending(categories) }
    }

    override fun getSpendingsFlow(): Flow<List<Spending>> {
        return fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .snapshots()
            .map { it.toObjects(SpendingRemote::class.java) }
            .map { it to getCategories() }
            .map { (spendingRemote, categories) -> spendingRemote.map { it.toSpending(categories) } }
    }

    private suspend fun getCategories(): List<Category> {
        return fireStore
            .collection(mainCollectionPath)
            .document("preferences")
            .collection("categories")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(CategoryRemote::class.java) }
            .map { it.toCategory() }
    }

    @Deprecated("")
    override suspend fun getAllCategories(): List<Category> {
        val documentSnapshot = fireStore
            .collection(mainCollectionPath)
            .document("preferences")
            .collection("categories")
            .document("categories")
            .get()
            .await()

        val remoteCategories: Map<String, Any> = documentSnapshot.data ?: mapOf()
        return remoteCategories.map {
            val categoryRemote = CategoryRemote(it.key, it.value as String?)
            Timber.d("categoryRemote == $categoryRemote")
            categoryRemote.toCategory()
        }
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return fireStore
            .collection(mainCollectionPath)
            .document("preferences")
            .collection("categories")
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
            .collection(mainCollectionPath)
            .document("preferences")
            .collection("categories")
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
        val start = LocalTime.now().toNanoOfDay()
        val popularCategoryID = dataStore.data
            .catch { Timber.e("error dataStore.data get POPULAR_CATEGORY_ID == ${it.message}") }
            .first()[POPULAR_CATEGORY_ID]
        val result = LocalTime.now().toNanoOfDay().minus(start)
        Timber.e("aaaa getPopularCategory1 result == ${result/1000000}  ")
        return getCategories().firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }

    override suspend fun getPopularCategory2(): Category {
        val start = LocalTime.now().toNanoOfDay()
        val last15Spendings = getSpendings()
            .sortedByDescending { spending -> spending.date }
            .take(15) // take/follow last fresh spendings
        val popularCategoryID = last15Spendings
            .groupBy { it.category.id }
            .maxByOrNull { it.value.size }
            ?.key
        val result = LocalTime.now().toNanoOfDay().minus(start)
        Timber.e("aaaa getPopularCategory2 result == ${result/1000000}")
        return getCategories().firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }
}
