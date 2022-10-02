package com.shjman.polygon2.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


data class SpendingRemote(
    val uuid: String? = null,
    val date: String? = null,
    val category: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

data class Spending(
    val uuid: String,
    val date: LocalDateTime,
    val category: String? = null,
    val spentAmount: Int,
    val currency: String? = null,
    val note: String,
)

fun SpendingRemote.toSpending(): Spending {
    return Spending(
        uuid = this.uuid ?: (this.date + UUID.randomUUID()),
        date = convertDateStringToLocalDateTime(this.date),
        category = this.category,
        spentAmount = this.spentAmount ?: 0,
        currency = this.currency,
        note = this.note ?: "",
    )
}

fun convertDateStringToLocalDateTime(date: String?): LocalDateTime = LocalDateTime.parse(
    date,
    DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER),
)

const val LOCALE_DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss"