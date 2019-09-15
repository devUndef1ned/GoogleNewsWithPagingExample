package com.devundefined.googlenewswithpagingexample.infrastructure

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoader
import com.devundefined.googlenewswithpagingexample.domain.loader.LoadResult
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.ArticleDto
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsApi
import kotlinx.coroutines.*
import java.util.*

class ArticleLoaderImpl(private val newsApi: NewsApi, private val apiKey: String, private val country: String) : ArticleLoader {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val toModel: (ArticleDto) -> Article = { dto ->
        with(dto) { Article(source.name, author, title, description, url, imageUrl, Date()) }
    }

    override fun loadArticles(countPerPage: Int, pageNumber: Int): LoadResult {
        return runBlocking {
            withContext(scope.coroutineContext) {
                val result = newsApi.getNews(apiKey, country, countPerPage, pageNumber)
                if (result.status != "ok") {
                    LoadResult.Error(IllegalStateException(result.message))
                } else {
                    LoadResult.Data(result.articles.map(toModel), result.totalResult!!)
                }
            }
        }
    }
}