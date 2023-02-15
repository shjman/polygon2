package com.shjman.polygon2.repository

interface LogRepository {
    fun logNonFatalCrash(throwable: Throwable)
}