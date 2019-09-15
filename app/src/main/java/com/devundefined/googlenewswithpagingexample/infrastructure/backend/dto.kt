package com.devundefined.googlenewswithpagingexample.infrastructure.backend

import com.google.gson.annotations.SerializedName

class NewsDto(
    val status: String,
    val code: String?,
    val message: String?,
    val totalResult: Int?,
    val articles: List<ArticleDto>
)

class ArticleDto(
    val source: SourceDto,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    @SerializedName("urlToImage")
    val imageUrl: String,
    @SerializedName("publishedAt")
    val date: String
)

class SourceDto(val id: String?, val name: String)