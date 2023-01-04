package com.shjman.polygon2.data

data class CategoryRemote(
    val id: String? = null,
    val name: String? = null,
)

data class Category(
    val id: String,
    val name: String = "",
) {
    companion object {
        fun empty() = Category(
            id = "",
            name = "empty category",
        )
    }
}

fun CategoryRemote.toCategory() = Category(
    id = this.id ?: "",
    name = this.name ?: "empty category",
)
