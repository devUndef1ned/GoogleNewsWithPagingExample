package com.devundefined.googlenewswithpagingexample.domain.repository

import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult

interface ArticleRepository {
    fun getPage(pageNumber: Int, pageSize: Int): ArticlePageResult.PagedData
    fun savePage(pagedData: ArticlePageResult.PagedData): ArticlePageResult.PagedData
    fun getTimestampForPage(pageNumber: Int, pageSize: Int): Long
}