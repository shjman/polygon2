package com.shjman.polygon2

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SpentRepositoryImpl(private val fireStore: FirebaseFirestore) : SpentRepository {

    override suspend fun saveSpentAmount(spentAmount: Int) {
        val newData = mutableMapOf<String, Any>()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val currentLocalDateTime = LocalDateTime.now()
        val currentDateTimeString = currentLocalDateTime.format(formatter)
        newData["date"] = currentDateTimeString
        newData["spentAmount"] = spentAmount
        newData["category"] = "home"
        newData["currency"] = "zl"
        Timber.d("newData == $newData")

        fireStore
            .collection("family")
            .document(currentDateTimeString)
            .set(newData)
            .await()
    }

    override suspend fun getAllSpending() {
        val querySnapshot = fireStore
            .collection("family")
            .get()
            .await()

        val documents = querySnapshot.documents
        documents.onEach {
            val spendingRemote = it.toObject(SpendingRemote::class.java)
            Timber.d("spendingRemote == $spendingRemote")
        }
    }
}
