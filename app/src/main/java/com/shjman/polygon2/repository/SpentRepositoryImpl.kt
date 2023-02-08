package com.shjman.polygon2.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.*
import kotlinx.coroutines.delay
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
        private val SHARED_DOCUMENT_PATH = stringPreferencesKey("DOCUMENT_PATH")
    }

    override suspend fun addTrustedUser(trustedUserEmail: String, onError: (errorText: String) -> Unit) {
        try {
            val newData = mutableMapOf<String, String>()
            newData["email"] = trustedUserEmail
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_TRUSTED_EMAILS)
                .document(trustedUserEmail)
                .set(newData)
                .await()
        } catch (exception: Exception) {
            Timber.e("addTrustedUser exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override fun checkIsUserSignIn() = firebaseAuth.currentUser != null

    override suspend fun getCategories(onError: (errorText: String) -> Unit): List<Category> {
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_CATEGORIES)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(CategoryRemote::class.java) }
                .map { it.toCategory() }
        } catch (exception: Exception) {
            Timber.e("getCategories exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            emptyList()
        }
    }

    override suspend fun getCategoriesFlow(onError: (errorText: String) -> Unit): Flow<List<Category>> {
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_CATEGORIES)
                .snapshots()
                .map { it.toObjects(CategoryRemote::class.java) }
                .map { it.map { categoryRemote -> categoryRemote.toCategory() } }
        } catch (exception: Exception) {
            Timber.e("getCategoriesFlow exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            emptyFlow() // todo it work as all time loading process
        }
    }

    override fun getCurrentUserData(onError: (errorText: String) -> Unit): FirebaseUser? {
        return try {
            firebaseAuth.currentUser
        } catch (exception: Exception) {
            Timber.e("getCurrentUserData exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            null
        }
    }

    override suspend fun getDocumentPath(onError: (errorText: String) -> Unit): String {
        val documentPath = if (BuildConfig.mainCollectionPath == "testing_family") { // todo its a place to fix
            val sharedDocumentPath = dataStore.data
                .catch {
                    Timber.e("getDocumentPath exception == ${it.stackTraceToString()}")
                    onError(it.toString())
                }
                .first()[SHARED_DOCUMENT_PATH]
            sharedDocumentPath ?: getCurrentUserData(onError)?.uid ?: "" // todo think about this place
        } else {
            BuildConfig.mainCollectionPath
        }
        return documentPath
    }

    override suspend fun getPopularCategory(onError: (errorText: String) -> Unit): Category {
        val popularCategoryID = dataStore.data
            .catch {
                Timber.e("getPopularCategory dataStore.data get POPULAR_CATEGORY_ID == ${it.stackTraceToString()}")
                onError(it.toString())
            }
            .first()[POPULAR_CATEGORY_ID]
        return getCategories(onError = onError).firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }

    override suspend fun getSpending(
        localDateTime: LocalDateTime,
        onError: (errorText: String) -> Unit,
    ): Spending? {
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(SpendingRemote::class.java) }
                .firstOrNull { localDateTime.isEqual(convertDateStringToLocalDateTime(it.date)) }
                ?.toSpending(getCategories(onError = onError))
        } catch (exception: Exception) {
            Timber.e("getSpending exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            null
        }
    }

    override suspend fun getSpendings(onError: (errorText: String) -> Unit): List<Spending> {
        val categories = getCategories(onError = onError)
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(SpendingRemote::class.java) }
                .map { it.toSpending(categories) }
        } catch (exception: Exception) {
            Timber.e("getSpendings exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            emptyList()
        }
    }

    override suspend fun getSpendingsFlow(onError: (errorText: String) -> Unit): Flow<List<Spending>> {
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .snapshots()
                .map { it.toObjects(SpendingRemote::class.java) }
                .map { it to getCategories(onError = onError) }
                .map { (spendingRemote, categories) -> spendingRemote.map { it.toSpending(categories) } }
        } catch (exception: Exception) {
            Timber.e("getSpendingsFlow exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            emptyFlow()
        }
    }

    override suspend fun getTrustedUsers(onError: (errorText: String) -> Unit): Flow<List<TrustedUser>> {
        delay(BuildConfig.testDelayDuration)
        return try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_TRUSTED_EMAILS)
                .snapshots()
                .map { it.toObjects(TrustedUser::class.java) }
        } catch (exception: Exception) {
            Timber.e("getTrustedUsers exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
            emptyFlow()
        }
    }

    override suspend fun isUserOwner(onError: (errorText: String) -> Unit): Flow<Boolean> {
        return dataStore.data
            .catch {
                Timber.e("isUserOwner exception == ${it.stackTraceToString()}")
                onError(it.toString())
            }
            .map { it[SHARED_DOCUMENT_PATH] }
            .map { it == null }
    }

    override suspend fun removeSharedDocumentPath(onError: (errorText: String) -> Unit) {
        try {
            dataStore.edit { preferences ->
                preferences.remove(SHARED_DOCUMENT_PATH)
            }
        } catch (exception: Exception) {
            Timber.e("removeSharedDocumentPath exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override suspend fun removeSpending(
        onError: (errorText: String) -> Unit,
        uuid: String,
    ) {
        try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .document(uuid)
                .delete()
        } catch (exception: Exception) {
            Timber.e("removeSpending exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override suspend fun saveCategory(
        category: Category,
        onError: (errorText: String) -> Unit,
    ) {
        val newData = mutableMapOf<String, String>()
        val id = category.id
        newData["id"] = id
        newData["name"] = category.name

        try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_CATEGORIES)
                .document(id)
                .set(newData)
                .await()
        } catch (exception: Exception) {
            Timber.e("saveCategory exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override suspend fun updateSharedDocumentPath(documentPath: String) {
        dataStore.edit { preferences ->
            preferences[SHARED_DOCUMENT_PATH] = documentPath
        }
    }

    override suspend fun saveSpending(
        category: Category,
        note: String,
        onError: (errorText: String) -> Unit,
        spentAmount: Int,
    ) {
        delay(BuildConfig.testDelayDuration)
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

        try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .document(uuid)
                .set(newData)
                .await()
        } catch (exception: Exception) {
            Timber.e("saveSpending exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override suspend fun signOut() {
        dataStore.edit {
            it.clear()
        }
        firebaseAuth.signOut()
    }

    override suspend fun updateDataAfterSuccessSignIn(onError: (errorText: String) -> Unit) {
        getCurrentUserData(onError)?.email?.let { emailOwner ->
            val newData = mutableMapOf<String, String>()
            newData["email_owner"] = emailOwner
            try {
                fireStore
                    .collection(COLLECTION_ENTRY_POINT)
                    .document(getDocumentPath(onError))
                    .set(newData)
                    .await()
            } catch (exception: Exception) {
                Timber.e("updateDataAfterSuccessSignIn exception == ${exception.stackTraceToString()}")
                onError(exception.toString())
            }
        }
    }

    override suspend fun updatePopularCategoryID(
        onError: (errorText: String) -> Unit,
        popularCategoryID: String,
    ) {
        try {
            dataStore.edit { preferences ->
                preferences[POPULAR_CATEGORY_ID] = popularCategoryID
            }
        } catch (exception: Exception) {
            Timber.e("updatePopularCategoryID exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override suspend fun updateSpending(
        onError: (errorText: String) -> Unit,
        spending: Spending,
        showSpendingUpdated: MutableSharedFlow<Unit>,
    ) {
        val newData = mutableMapOf<String, Any>()
        newData["uuid"] = spending.uuid
        newData["date"] = spending.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
        newData["spentAmount"] = spending.spentAmount
        newData["categoryID"] = spending.category.id
        newData["note"] = spending.note

        try {
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(getDocumentPath(onError))
                .collection(COLLECTION_SPENDINGS)
                .document(spending.uuid)
                .set(newData)
                .await()
        } catch (exception: Exception) {
            Timber.e("updateSpending exception == ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
        showSpendingUpdated.emit(Unit)
    }
}
