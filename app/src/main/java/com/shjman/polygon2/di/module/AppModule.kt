package com.shjman.polygon2.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.repository.SpentRepositoryImpl
import com.shjman.polygon2.ui.*
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import com.shjman.polygon2.ui.settings.AddTrustedUserViewModel
import com.shjman.polygon2.ui.settings.SettingViewModel
import com.shjman.polygon2.ui.settings.SharingSettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SpentRepository> {
        SpentRepositoryImpl(
            dataStore = get(),
            firebaseAuth = get(),
            fireStore = get(),
        )
    }

    single { provideFirebaseAuth() }
    single { provideFireStore() }
    single { provideDataStore(get()) }

    viewModel { CategoriesViewModel(get()) }
    viewModel { EditCategoryViewModel(get()) }
    viewModel { EditSpendingViewModel(get()) }
    viewModel { AddTrustedUserViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SettingViewModel(get()) }
    viewModel { SharingSettingViewModel(get()) }
    viewModel { SpentViewModel(get()) }
}

private fun provideFirebaseAuth() = FirebaseAuth.getInstance()
private fun provideFireStore() = Firebase.firestore

private fun provideDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("user_preferences") }
    )
}