package com.devundefined.googlenewswithpagingexample.domain.loader

interface ArticleLoaderProcessor {
    fun processLoadArticles(countPerPage: Int, pageNumber: Int): LoadResult
}