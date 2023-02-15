package com.shjman.polygon2.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.LogRepositoryImpl
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.repository.SpentRepositoryImpl
import com.shjman.polygon2.ui.EntryPointViewModel
import com.shjman.polygon2.ui.overview.OverviewViewModel
import com.shjman.polygon2.ui.spent.SpentViewModel
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import com.shjman.polygon2.ui.edit_spending.EditSpendingViewModel
import com.shjman.polygon2.ui.home.HomeViewModel
import com.shjman.polygon2.ui.settings.AddTrustedUserViewModel
import com.shjman.polygon2.ui.settings.SettingViewModel
import com.shjman.polygon2.ui.settings.SharingSettingViewModel
import com.shjman.polygon2.ui.unauthorized.UnauthorizedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance() }
    single { Firebase.firestore }
    single { provideDataStore(get()) }
    single<LogRepository> { LogRepositoryImpl() }
    single<SpentRepository> {
        SpentRepositoryImpl(
            dataStore = get(),
            firebaseAuth = get(),
            fireStore = get(),
        )
    }

    viewModel { AddTrustedUserViewModel(get(), get()) }
    viewModel { CategoriesViewModel(get(), get()) }
    viewModel { EditCategoryViewModel(get(), get()) }
    viewModel { EditSpendingViewModel(get(), get()) }
    viewModel { EntryPointViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { OverviewViewModel(get(), get()) }
    viewModel { SettingViewModel(get(), get()) }
    viewModel { SharingSettingViewModel(get(), get()) }
    viewModel { SpentViewModel(get(), get()) }
    viewModel { UnauthorizedViewModel(get(), get()) }
}

private fun provideDataStore(context: Context) = PreferenceDataStoreFactory.create(
    produceFile = { context.preferencesDataStoreFile("user_preferences") }
)
