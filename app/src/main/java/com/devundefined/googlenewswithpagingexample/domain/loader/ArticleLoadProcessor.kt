package com.devundefined.googlenewswithpagingexample.domain.loader

interface ArticleLoadProcessor {
    fun processLoading(pageNumber: Int = 1, pageSize: Int): LoadResult
}