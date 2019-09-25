package com.devundefined.googlenewswithpagingexample.domain.loader

import com.devundefined.googlenewswithpagingexample.domain.Article

sealed class LoadResult {
    class Error(val cause: Throwable) : LoadResult()
    class Data(
        val pagedArticles: List<Article>,
        val totalCount: Int,
        val countPerPage: Int,
        var currentPage: Int = 1
    ) : LoadResult()
}
