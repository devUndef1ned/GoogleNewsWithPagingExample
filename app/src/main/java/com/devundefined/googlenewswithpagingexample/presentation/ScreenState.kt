package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult

class ScreenState(
    val currentList: List<Article> = listOf(),
    val currentPage: ArticlePageResult.PagedData? = null
) {
    companion object {
        fun createInitial(articleLoaderPageResult: ArticlePageResult.PagedData) =
            ScreenState(
                mutableListOf(*articleLoaderPageResult.data.toTypedArray()),
                articleLoaderPageResult
            )

        fun create(list: List<Article>, currentPage: ArticlePageResult.PagedData) =
            ScreenState(
                list,
                currentPage
            )
    }

    fun isInitialized() = currentList.isNotEmpty()

    fun mutate(pagedData: ArticlePageResult.PagedData) =
        ScreenState(
            currentList + pagedData.data,
            pagedData
        )
}