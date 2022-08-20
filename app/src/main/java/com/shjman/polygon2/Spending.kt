package com.shjman.polygon2

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class SpendingRemote(
    val date: String? = null,
    val category: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

data class Spending(
    val date: LocalDateTime? = null,
    val category: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

fun SpendingRemote.toSpending(): Spending {
    val localDateTime = LocalDateTime.parse(
        this.date,
        DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER),
    )
    return Spending(
        date = localDateTime,
        category = this.category,
        spentAmount = this.spentAmount,
        currency = this.currency,
        note = this.note,
    )
}

const val LOCALE_DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss"