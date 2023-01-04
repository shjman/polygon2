package com.shjman.polygon2.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SpentRepositoryImpl(
    private val fireStore: FirebaseFirestore,
) : SpentRepository {

    private var categoriesCache: List<Category>? = null

    companion object {
        const val mainCollectionPath = BuildConfig.mainCollectionPath
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
        val querySnapshot = fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .get()
            .await()

        val documents = querySnapshot.documents

        return documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .firstOrNull { localDateTime.isEqual(convertDateStringToLocalDateTime(it.date)) }
            ?.toSpending(getCategories())
    }

    override fun getSpendingsFlow(): Flow<List<Spending>> {
        return fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .snapshots()
            .map { it.toObjects(SpendingRemote::class.java) }
            .map { it.map { spendingRemote -> spendingRemote.toSpending(getCategories()) } }
    }

    private suspend fun getCategories(): List<Category> {
        if (categoriesCache != null) { // todo test it upgrade
            return categoriesCache as List<Category>
        }
        val querySnapshot = fireStore
            .collection(mainCollectionPath)
            .document("preferences")
            .collection("categories")
            .get()
            .await()

        val documents = querySnapshot.documents

        categoriesCache = documents
            .mapNotNull { it.toObject(CategoryRemote::class.java) }
            .map { it.toCategory() }
        return categoriesCache as List<Category>
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
}
