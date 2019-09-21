package com.devundefined.googlenewswithpagingexample.domain

import java.util.*

data class Article(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val date: Date
) {
    val id: String = sourceName.hashCode().toString() + title.hashCode().toString() + date.hashCode().toString()
}