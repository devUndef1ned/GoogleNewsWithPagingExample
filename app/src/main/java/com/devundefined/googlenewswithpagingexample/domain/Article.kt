package com.devundefined.googlenewswithpagingexample.domain

import java.util.*

class Article(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val date: Date
) {
    val id: String = sourceName.hashCode().toString() + title.hashCode().toString() + date.hashCode().toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.hashCode()
}