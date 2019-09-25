package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult
import com.devundefined.googlenewswithpagingexample.domain.Source
import java.util.*

fun getId(pageNumber: Int, pageSize: Int): String {
    return "#$pageNumber/size$pageSize"
}

val pageToModel: ArticlePageEntity.() -> ArticlePageResult.PagedData = {
    ArticlePageResult.PagedData(
        data.map(articleToModel),
        totalCount,
        Source.LOCAL,
        pageSize,
        pageNumber
    )
}

val pageToEntity: ArticlePageResult.PagedData.() -> ArticlePageEntity = {
    ArticlePageEntity(
        getId(currentPage, pageSize),
        currentPage,
        pageSize,
        totalCount,
        data.map(articleToEntity),
        System.currentTimeMillis()
    )
}

val articleToModel: ArticleEntity.() -> Article = {
    Article(sourceName, author, title, description, url, imageUrl, Date(dateTime))
}

val articleToEntity: Article.() -> ArticleEntity = {
    ArticleEntity(sourceName, author, title, description, url, imageUrl, date.time)
}