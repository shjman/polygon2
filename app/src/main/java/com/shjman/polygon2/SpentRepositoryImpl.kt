package com.shjman.polygon2

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber


class SpentRepositoryImpl(private val fireStore: FirebaseFirestore) : SpentRepository {

    override suspend fun saveSpentAmount(spentAmount: Int) {
        val newData = mutableMapOf<String, Any>()
        val dateTime = DateTime()
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val currentDateTimeInString = dateTimeFormatter.print(dateTime)
        newData["date"] = currentDateTimeInString
        newData["spentAmount"] = spentAmount
        newData["category"] = "home"
        newData["currency"] = "zl"
        Timber.d("newData == $newData")

        fireStore
            .collection("family")
            .document(currentDateTimeInString)
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
