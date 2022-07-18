package com.shjman.polygon2


data class SpendingRemote(
    val date: String? = null,
    val category: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

data class Spending(
    val date: String? = null,
    val category: String? = null,
    val spentAmount: Int? = null,
    val currency: String? = null,
    val note: String? = null,
)

fun SpendingRemote.toSpending() = Spending(
    date = this.date,
    category = this.category,
    spentAmount = this.spentAmount,
    currency = this.currency,
    note = this.note,
)