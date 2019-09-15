package com.devundefined.googlenewswithpagingexample.domain.loader

import com.devundefined.googlenewswithpagingexample.domain.Article

sealed class LoadResult {
    class Error(val cause: Throwable) : LoadResult()
    class Data(val articles: List<Article>, val totalCount: Int) : LoadResult()
}
