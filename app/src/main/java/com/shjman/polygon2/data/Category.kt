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
            id = "id of the empty category",
            name = "empty category",
        )
    }
}

fun CategoryRemote.toCategory() = Category(
    id = this.id ?: Category.empty().id,
    name = this.name ?: Category.empty().name,
)
