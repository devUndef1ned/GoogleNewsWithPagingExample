package com.devundefined.googlenewswithpagingexample.domain

sealed class ArticlePageResult {

    class PagedData(
        val data: List<Article>,
        val totalCount: Int,
        val source: Source,
        val pageSize: Int,
        val currentPage: Int = 1
    ) : ArticlePageResult() {
        val isFinished = totalCount <= pageSize * (currentPage - 1) + data.size
    }

    class Error(val cause: Throwable) : ArticlePageResult()
}

enum class Source {
    NETWORK, LOCAL
}