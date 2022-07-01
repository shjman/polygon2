package com.shjman.polygon2

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import timber.log.Timber


class SpentRepository {

    private val fireStore = Firebase.firestore

    suspend fun saveSpentAmount(spentAmount: Int) {
        val result = fireStore
            .collection("family")
            .document("K0sLqKCYoHGpcGLTEEzY")
            .get()
            .await()
        val resultData = result.data ?: mutableMapOf<String, Any>()
        val dateTime = DateTime()
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd/M/yyyy hh:mm:ss")
        val currentDateTimeInString = dateTimeFormatter.print(dateTime)
        Timber.d("old values == $resultData")
        resultData[currentDateTimeInString] = spentAmount
        Timber.d("new values == $resultData")

        fireStore
            .collection("family")
            .document("K0sLqKCYoHGpcGLTEEzY")
            .set(resultData)
            .await()
//        Timber.d("aaa 1")
    }
}
