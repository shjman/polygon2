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
                .document(getDocumentPath())
                .collection(COLLECTION_TRUSTED_EMAILS)
                .document(trustedUserEmail)
                .set(newData)
                .await()
        } catch (exception: Exception) {
            Timber.e("addTrustedUser ${exception.stackTraceToString()}")
            onError(exception.toString())
        }
    }

    override fun checkIsUserSignIn() = firebaseAuth.currentUser != null

    override suspend fun getCategories(): List<Category> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_CATEGORIES)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(CategoryRemote::class.java) }
            .map { it.toCategory() }
    }

    override suspend fun getCategoriesFlow(): Flow<List<Category>> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_CATEGORIES)
            .snapshots()
            .map { it.toObjects(CategoryRemote::class.java) }
            .map { it.map { categoryRemote -> categoryRemote.toCategory() } }
    }

    override fun getCurrentUserData(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw Exception("wtf?? firebaseAuth.currentUser == null !") // todo error handling
    }

    override suspend fun getDocumentPath(): String {
        val documentPath = if (BuildConfig.mainCollectionPath == "testing_family") { // todo its a place to fix
            val sharedDocumentPath = dataStore.data
                .catch { Timber.e("error dataStore.data get SHARED_DOCUMENT_PATH == ${it.message}") }
                .first()[SHARED_DOCUMENT_PATH]
            sharedDocumentPath ?: this.getCurrentUserData().uid
        } else {
            BuildConfig.mainCollectionPath
        }
        Timber.d("documentPath == $documentPath")
        return documentPath
    }

    override suspend fun getPopularCategory(): Category {
        val popularCategoryID = dataStore.data
            .catch { Timber.e("error dataStore.data get POPULAR_CATEGORY_ID == ${it.message}") }
            .first()[POPULAR_CATEGORY_ID]
        return getCategories().firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }

    override suspend fun getSpending(localDateTime: LocalDateTime): Spending? {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
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
            .document(getDocumentPath())
            .collection(COLLECTION_SPENDINGS)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .map { it.toSpending(categories) }
    }

    override suspend fun getSpendingsFlow(): Flow<List<Spending>> {
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_SPENDINGS)
            .snapshots()
            .map { it.toObjects(SpendingRemote::class.java) }
            .map { it to getCategories() }
            .map { (spendingRemote, categories) -> spendingRemote.map { it.toSpending(categories) } }
    }

    override suspend fun getTrustedUsers(): Flow<List<TrustedUser>> {
        delay(BuildConfig.testDelayDuration)
        return fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_TRUSTED_EMAILS)
            .snapshots()
            .map { it.toObjects(TrustedUser::class.java) }
    }

    override suspend fun isUserOwner(): Flow<Boolean> {
        return dataStore.data
            .catch { Timber.e("wtf error dataStore.data get $SHARED_DOCUMENT_PATH == ${it.message}") }
            .map { it[SHARED_DOCUMENT_PATH] }
            .map { it == null }
    }

    override suspend fun removeSharedDocumentPath() {
        dataStore.edit { preferences ->
            preferences.remove(SHARED_DOCUMENT_PATH)
        }
    }

    override suspend fun removeSpending(uuid: String) {
        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_SPENDINGS)
            .document(uuid)
            .delete()
    }

    override suspend fun saveCategory(category: Category) {
        val newData = mutableMapOf<String, String>()
        val id = category.id
        newData["id"] = id
        newData["name"] = category.name

        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_CATEGORIES)
            .document(id)
            .set(newData)
            .await()
    }

    override suspend fun updateSharedDocumentPath(documentPath: String) {
        dataStore.edit { preferences ->
            preferences[SHARED_DOCUMENT_PATH] = documentPath
        }
    }

    override suspend fun saveSpentAmount(spentAmount: Int, note: String, category: Category) {
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

        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .collection(COLLECTION_SPENDINGS)
            .document(uuid)
            .set(newData)
            .await()
    }

    override suspend fun signOut() {
        dataStore.edit {
            it.clear()
        }
        firebaseAuth.signOut()
    }

    override suspend fun updateDataAfterSuccessSignIn() {
        val emailOwner = getCurrentUserData().email ?: throw Exception("currentUser.email == null") // todo error handling
        val newData = mutableMapOf<String, String>()
        newData["email_owner"] = emailOwner
        fireStore
            .collection(COLLECTION_ENTRY_POINT)
            .document(getDocumentPath())
            .set(newData)
    }

    override suspend fun updatePopularCategoryID(popularCategoryID: String) {
        dataStore.edit { preferences ->
            preferences[POPULAR_CATEGORY_ID] = popularCategoryID
        }
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
            .document(getDocumentPath())
            .collection(COLLECTION_SPENDINGS)
            .document(spending.uuid)
            .set(newData)
        showSpendingUpdated.emit(Unit)
    }
}
