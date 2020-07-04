package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult

class ScreenState(
    val totalSize: Int = 0,
    val currentList: List<Article> = listOf(),
    val currentPage: ArticlePageResult.PagedData? = null
) {
    companion object {
        fun createInitial(articleLoaderPageResult: ArticlePageResult.PagedData) =
            ScreenState(
                articleLoaderPageResult.totalCount,
                mutableListOf(*articleLoaderPageResult.data.toTypedArray()),
                articleLoaderPageResult
            )
    }

    fun isInitialized() = currentList.isNotEmpty()

    fun mutate(pagedData: ArticlePageResult.PagedData) =
        ScreenState(
            totalSize,
            currentList + pagedData.data,
            pagedData
        )
}