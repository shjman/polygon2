package com.shjman.polygon2.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
            .document(currentDateTimeString)
            .set(newData)
            .await()
    }

    override suspend fun getSpending(localDateTime: LocalDateTime): Spending? {
        val querySnapshot = fireStore.collection(mainCollectionPath)
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
