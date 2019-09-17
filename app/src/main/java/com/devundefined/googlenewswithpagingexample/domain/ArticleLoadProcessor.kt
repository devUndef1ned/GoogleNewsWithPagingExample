package com.devundefined.googlenewswithpagingexample.domain

interface ArticleLoadProcessor {
    fun processLoading(pageNumber: Int = 1): LoadResult
}