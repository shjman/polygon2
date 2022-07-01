package com.shjman.polygon2.di.module

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shjman.polygon2.SpentRepository
import com.shjman.polygon2.SpentRepositoryImpl
import com.shjman.polygon2.SpentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SpentRepository> { SpentRepositoryImpl(get()) }
    single { provideFireStore() }

    viewModel { SpentViewModel(get()) }
}

private fun provideFireStore() = Firebase.firestore
