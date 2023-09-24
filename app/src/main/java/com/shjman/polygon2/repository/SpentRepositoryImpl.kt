package com.shjman.polygon2.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.CategoryRemote
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.data.SpendingRemote
import com.shjman.polygon2.data.TrustedUser
import com.shjman.polygon2.data.convertDateStringToLocalDateTime
import com.shjman.polygon2.data.toCategory
import com.shjman.polygon2.data.toSpending
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
        private val IS_USER_OBSERVE_SOMEBODY = booleanPreferencesKey("IS_USER_OBSERVE_SOMEBODY")
    }

    override suspend fun addTrustedUser(trustedUserEmail: String) {
        val newData = mutableMapOf<String, String>()
        newData["email"] = trustedUserEmail
        getEntryPoint()
            .collection(COLLECTION_TRUSTED_EMAILS)
            .document(trustedUserEmail)
            .set(newData)
            .await()
    }

    override fun checkIsUserSignIn(): Boolean {
//        throw Exception()
        return firebaseAuth.currentUser != null
    }

    override suspend fun getCategories(): List<Category> {
        return getEntryPoint()
            .collection(COLLECTION_CATEGORIES)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(CategoryRemote::class.java) }
            .map { it.toCategory() }
    }

    override suspend fun getCategoriesFlow(): Flow<List<Category>> {
        return getEntryPoint()
            .collection(COLLECTION_CATEGORIES)
            .snapshots()
            .map { it.toObjects(CategoryRemote::class.java) }
            .map { it.map { categoryRemote -> categoryRemote.toCategory() } }
    }

    private fun getCollectionSpendingsPath(): String {
        return if (BuildConfig.mainCollectionPath == "testing_family") {
            COLLECTION_SPENDINGS
        } else {
            "spending"
        }
    }

    override fun getCurrentUserData(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw Exception("firebaseAuth.currentUser == null")
    }

    override suspend fun getDocumentPath(): String {
        val documentPath = if (BuildConfig.mainCollectionPath == "testing_family") { // todo its a place to fix
            // for all
            if (dataStore.data.first()[IS_USER_OBSERVE_SOMEBODY] == true) {
                dataStore.data.first()[SHARED_DOCUMENT_PATH] ?: throw Exception("SHARED_DOCUMENT_PATH == null")
            } else {
                getCurrentUserData().uid
            }
        } else {
            // my custom own spendings single version
            BuildConfig.mainCollectionPath
        }
        return documentPath
    }

    private suspend fun getEntryPoint(): DocumentReference {
        return if (BuildConfig.mainCollectionPath == "testing_family") { // todo its a place to fix
            val documentPath = if (dataStore.data.first()[IS_USER_OBSERVE_SOMEBODY] == true) {
                dataStore.data.first()[SHARED_DOCUMENT_PATH] ?: throw Exception("SHARED_DOCUMENT_PATH == null")
            } else {
                getCurrentUserData().uid
            }
            fireStore
                .collection(COLLECTION_ENTRY_POINT)
                .document(documentPath)
        } else {
            // my custom own spendings single version
            fireStore
                .collection(BuildConfig.mainCollectionPath)
                .document("spending")
        }
    }

    override suspend fun getPopularCategory(): Category {
        val popularCategoryID = dataStore.data.first()[POPULAR_CATEGORY_ID]
        return getCategories().firstOrNull { it.id == popularCategoryID } ?: Category.empty()
    }

    override suspend fun getSpending(
        localDateTime: LocalDateTime,
    ): Spending? {
        return getEntryPoint()
            .collection(getCollectionSpendingsPath())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .firstOrNull { localDateTime.isEqual(convertDateStringToLocalDateTime(it.date)) }
            ?.toSpending(getCategories())
    }

    override suspend fun getSpendings(): List<Spending> {
        val categories = getCategories()
        return getEntryPoint()
            .collection(getCollectionSpendingsPath())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(SpendingRemote::class.java) }
            .map { it.toSpending(categories) }
    }

    override suspend fun getSpendingsFlow(): Flow<List<Spending>> {
        return getEntryPoint()
            .collection(getCollectionSpendingsPath())
            .snapshots()
            .map { it.toObjects(SpendingRemote::class.java) }
            .map { it to getCategories() }
            .map { (spendingRemote, categories) -> spendingRemote.map { it.toSpending(categories) } }
    }

    override suspend fun getTrustedUsers(): Flow<List<TrustedUser>> {
        delay(BuildConfig.testDelayDuration)
        return getEntryPoint()
            .collection(COLLECTION_TRUSTED_EMAILS)
            .snapshots()
            .map { it.toObjects(TrustedUser::class.java) }
    }

    override suspend fun isUserObserveSomebody(): Flow<Boolean> {
        delay(BuildConfig.testDelayDuration)
        return dataStore.data
            .map { it[IS_USER_OBSERVE_SOMEBODY] }
            .map { it == true }  // check if we observe somebody, or we observe ourself
    }

    override suspend fun removeSharedDocumentPath() {
        dataStore.edit { preferences ->
            preferences.remove(SHARED_DOCUMENT_PATH)
            preferences[IS_USER_OBSERVE_SOMEBODY] = false
        }
    }

    override suspend fun removeSpending(
        uuid: String,
    ) {
        getEntryPoint()
            .collection(getCollectionSpendingsPath())
            .document(uuid)
            .delete()
    }

    override suspend fun saveCategory(
        category: Category,
    ) {
        val newData = mutableMapOf<String, String>()
        val id = category.id
        newData["id"] = id
        newData["name"] = category.name

        getEntryPoint()
            .collection(COLLECTION_CATEGORIES)
            .document(id)
            .set(newData)
            .await()
    }

    override suspend fun updateSharedDocumentPath(sharedDocumentPath: String) {
        dataStore.edit { preferences ->
            preferences[SHARED_DOCUMENT_PATH] = sharedDocumentPath
            preferences[IS_USER_OBSERVE_SOMEBODY] = true
        }
    }

    override suspend fun saveSpending(
        category: Category,
        note: String,
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

        getEntryPoint()
            .collection(getCollectionSpendingsPath())
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
        getCurrentUserData().email?.let { emailOwner ->
            val newData = mutableMapOf<String, String>()
            newData["email_owner"] = emailOwner
            getEntryPoint()
                .set(newData)
                .await()
        }
    }

    override suspend fun updatePopularCategoryID(
        popularCategoryID: String,
    ) {
        dataStore.edit { preferences ->
            preferences[POPULAR_CATEGORY_ID] = popularCategoryID
        }
    }

    override suspend fun updateSpending(
        spending: Spending,
        showSpendingUpdated: MutableSharedFlow<Unit>,
    ) {
        val newData = mutableMapOf<String, Any>()
        newData["uuid"] = spending.uuid
        newData["date"] = spending.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
        newData["spentAmount"] = spending.spentAmount
        newData["categoryID"] = spending.category.id
        newData["note"] = spending.note

        getEntryPoint()
            .collection(getCollectionSpendingsPath())
            .document(spending.uuid)
            .set(newData)
            .await()
        showSpendingUpdated.emit(Unit)
    }
}
