package com.shjman.polygon2.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.repository.SpentRepositoryImpl
import com.shjman.polygon2.ui.EditSpendingViewModel
import com.shjman.polygon2.ui.HomeViewModel
import com.shjman.polygon2.ui.SpentViewModel
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SpentRepository> {
        SpentRepositoryImpl(
            fireStore = get(),
            dataStore = get(),
        )
    }

    single { provideFireStore() }
    single { provideDataStore(get()) }

    viewModel { HomeViewModel() }
    viewModel { SpentViewModel(get()) }
    viewModel { EditSpendingViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { EditCategoryViewModel(get()) }
}

private fun provideFireStore() = Firebase.firestore

private fun provideDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("user_preferences") }
    )
}