package com.shjman.polygon2

data class CategoryRemote(
    val id: String? = null,
    val name: String? = null,
)

data class Category(
    val id: String? = null,
    val name: String = "",
)

fun CategoryRemote.toCategory() = Category(
    id = this.id,
    name = this.name ?: "",
)
