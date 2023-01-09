package com.shjman.polygon2.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


data class SpendingRemote(
    val uuid: String? = null,
    val date: String? = null,
    @Deprecated("Use categoryId")
    val category: String? = null,
    val categoryID: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

data class Spending(
    val uuid: String,
    val date: LocalDateTime,
    val category: Category,
    val categoryID: String? = null,
    val spentAmount: Int,
    val currency: String? = null,
    val note: String,
)

fun SpendingRemote.toSpending(categories: List<Category>): Spending {
    return Spending(
        uuid = uuid ?: (date + UUID.randomUUID()),
        date = convertDateStringToLocalDateTime(date),
        category = getCategory(category, categoryID, categories), // todo remove the duplication category can be misleading where the source of the truth
        categoryID = categoryID,
        spentAmount = spentAmount ?: 0,
        currency = currency,
        note = note ?: "",
    )
}

fun getCategory(category: String?, categoryID: String?, categories: List<Category>): Category {
    return if (category != null) {
        categories.firstOrNull { it.name == category } ?: Category.empty() // to support legacy data
    } else {
        categories.firstOrNull { it.id == categoryID } ?: Category.empty()
    }
}

fun convertDateStringToLocalDateTime(date: String?): LocalDateTime = LocalDateTime.parse(
    date,
    DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER),
)

const val LOCALE_DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss"