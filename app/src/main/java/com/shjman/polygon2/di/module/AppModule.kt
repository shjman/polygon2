package com.shjman.polygon2.di.module

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.repository.SpentRepositoryImpl
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.EditSpendingViewModel
import com.shjman.polygon2.ui.SpentViewModel
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SpentRepository> { SpentRepositoryImpl(get()) }
    single { provideFireStore() }

    viewModel { SpentViewModel(get()) }
    viewModel { EditSpendingViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { EditCategoryViewModel(get()) }
}

private fun provideFireStore() = Firebase.firestore
