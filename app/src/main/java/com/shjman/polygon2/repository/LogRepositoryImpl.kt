package com.shjman.polygon2.repository

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class LogRepositoryImpl : LogRepository {
    override fun logNonFatalCrash(throwable: Throwable) {
        Timber.e(throwable.stackTraceToString())
        Firebase.crashlytics.recordException(throwable)
    }
}