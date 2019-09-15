package com.devundefined.googlenewswithpagingexample.domain.loader

import com.devundefined.googlenewswithpagingexample.domain.Article

interface ArticleLoader {

    fun loadArticles(countPerPage: Int, pageNumber: Int): LoadResult
}