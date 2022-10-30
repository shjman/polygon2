package com.shjman.polygon2.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SpentRepositoryImpl(
    private val fireStore: FirebaseFirestore,
) : SpentRepository {

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
        newData["category"] = category.name
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
            ?.toSpending()
    }

    override suspend fun getAllSpending(): List<Spending> {
        val querySnapshot = fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .get()
            .await()

        val documents = querySnapshot.documents

        return documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .map { it.toSpending() }
    }

    private suspend fun addUUIDToSpendings(remoteSpendings: List<SpendingRemote>) {
        remoteSpendings
            .filter { it.uuid == null }
            .onEach { addUUIDToSpending(it) }
    }

    private suspend fun addUUIDToSpending(spendingRemote: SpendingRemote) {
        val newSpendingData = mutableMapOf<String, Any>()
        newSpendingData["uuid"] = spendingRemote.date + UUID.randomUUID()
        newSpendingData["date"] = spendingRemote.date ?: ""
        newSpendingData["spentAmount"] = spendingRemote.spentAmount ?: 0
        newSpendingData["category"] = spendingRemote.category ?: ""
        newSpendingData["note"] = spendingRemote.note ?: ""

        fireStore
            .collection(mainCollectionPath)
            .document("spending")
            .collection("spending")
            .document(spendingRemote.date.toString())
            .update(newSpendingData)
            .await()
    }

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
}
