package com.shjman.polygon2.data

import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


data class SpendingRemote(
    val uuid: String? = null,
    val date: String? = null,
    @Deprecated("Use categoryId")
    @PropertyName("category")
    val categoryName: String? = null,
    val categoryID: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

data class Spending(
    val uuid: String,
    val date: LocalDateTime,
    val category: Category,
    val spentAmount: Int,
    val currency: String? = null,
    val note: String,
)

fun SpendingRemote.toSpending(categories: List<Category>): Spending {
    return Spending(
        uuid = uuid ?: (date + UUID.randomUUID()),
        date = convertDateStringToLocalDateTime(date),
        category = getCategory(categoryName, categoryID, categories),
        spentAmount = spentAmount ?: 0,
        currency = currency,
        note = note ?: "",
    )
}

fun getCategory(categoryName: String?, categoryID: String?, categories: List<Category>): Category {
    return if (categoryName != null) {
        categories.firstOrNull { it.name == categoryName } ?: Category.empty() // to support legacy data
    } else {
        categories.firstOrNull { it.id == categoryID } ?: Category.empty()
    }
}

fun convertDateStringToLocalDateTime(date: String?): LocalDateTime = LocalDateTime.parse(
    date,
    DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER),
)

const val LOCALE_DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss"