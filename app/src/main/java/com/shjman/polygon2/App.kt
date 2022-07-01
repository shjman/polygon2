package com.shjman.polygon2

import android.app.Application
import com.shjman.polygon2.di.module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}