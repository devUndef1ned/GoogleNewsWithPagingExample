package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository

class ArticleRepositoryImpl : ArticleRepository {

    private val data: MutableMap<String, ArticlePageEntity> = mutableMapOf()

    override fun getPage(pageNumber: Int, pageSize: Int): ArticlePageResult.PagedData {
        return data[getId(pageNumber, pageSize)]?.let(pageToModel)
            ?: throw IllegalStateException("Do not contain such page number $pageNumber, size $pageSize")
    }

    override fun savePage(pagedData: ArticlePageResult.PagedData): ArticlePageResult.PagedData {
        pagedData.let(pageToEntity).also { entity -> data[entity.id] = entity }
        return pagedData
    }

    override fun getTimestampForPage(pageNumber: Int, pageSize: Int): Long {
        return data[getId(pageNumber, pageSize)]?.timeStamp?: 0
    }
}

data class ArticlePageEntity(
    val id: String,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
    val data: List<ArticleEntity>,
    val timeStamp: Long
)

data class ArticleEntity(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val dateTime: Long
)