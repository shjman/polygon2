package com.shjman.polygon2.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong


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
    val spentAmount: Int,
    val currency: String? = null,
    val note: String,
) {
    companion object {
        fun preview() = Spending(
            uuid = UUID.randomUUID().toString(),
            date = LocalDateTime.now().minusMinutes(Random.nextULong(100u).toLong()),
            category = Category.empty(),
            spentAmount = Random.nextUInt(100u).toInt(),
            currency = null,
            note = UUID.randomUUID().toString(),
        )
    }
}

fun SpendingRemote.toSpending(categories: List<Category>): Spending {
    return Spending(
        uuid = uuid ?: (date + UUID.randomUUID()),
        date = convertDateStringToLocalDateTime(date),
        category = getCategory(category, categoryID, categories),
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