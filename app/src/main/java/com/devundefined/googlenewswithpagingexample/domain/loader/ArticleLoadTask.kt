package com.devundefined.googlenewswithpagingexample.domain.loader

import com.devundefined.googlenewswithpagingexample.domain.Article

sealed class ArticleLoadTaskResult {
    class Data(
        initialData: List<Article>,
        val totalCount: Int,
        private val loadMoreTask: () -> List<Article>
    ) : ArticleLoadTaskResult() {
        private val data: MutableList<Article> = mutableListOf(*initialData.toTypedArray())
        val isFinished: Boolean
            get() = data.size == totalCount

        @Throws
        fun loadMore(): List<Article> {
            return loadMoreTask().also { newData ->
                data.addAll(newData)
            }
        }
    }

    class Error(val cause: Throwable) : ArticleLoadTaskResult()
}