package com.devundefined.googlenewswithpagingexample.domain

sealed class ArticleLoadPageResult {

    class PagedData(
        val data: List<Article>,
        val totalCount: Int,
        countPerPage: Int,
        val currentPage: Int = 1
    ) : ArticleLoadPageResult() {
        val isFinished = totalCount <= countPerPage * (currentPage - 1) + data.size
    }

    class Error(val cause: Throwable) : ArticleLoadPageResult()
}