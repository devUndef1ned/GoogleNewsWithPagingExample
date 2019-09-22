package com.devundefined.googlenewswithpagingexample.infrastructure

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.LoadResult
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.ArticleDto
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsApi
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsDto
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.SourceDto
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ArticleLoadProcessorTest {

    @Mock
    lateinit var newsApi: NewsApi

    private val loader by lazy { ArticleLoadProcessorImpl(newsApi, "apiKey", "country") }

    @Test
    fun shouldReturnArticles_whenLoadSuccessfullyFromApi() {
        runBlocking {
            val dto1 = ArticleDto(
                SourceDto("sourceId1", "source1"),
                "author1",
                "title1",
                "description1",
                "url1",
                "imageUrl1",
                "2019-09-13T18:27:00Z"
            )
            val dto2 = ArticleDto(
                SourceDto("sourceId2", "source2"),
                "author2",
                "title2",
                "description2",
                "url2",
                "imageUrl2",
                "2019-09-13T18:14:04Z"
            )
            whenever(newsApi.getNews(any(), any(), any(), any())).thenReturn(
                NewsDto(
                    status = "ok",
                    code = null,
                    message = null,
                    totalResults = 2,
                    articles = listOf(dto1, dto2)
                )
            )

            val result = loader.processLoading()

            assertTrue(result is LoadResult.Data)
            (result as LoadResult.Data).pagedArticles.also { loadedArticles ->
                assertTrue(loadedArticles.any { it.matches(dto1) })
                assertTrue(loadedArticles.any { it.matches(dto2) })
            }
        }
    }

    @Test
    fun shouldReturnErrorResult_whenLoadThroughApiFailed() {
        runBlocking {
            whenever(
                newsApi.getNews(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).thenThrow(IllegalArgumentException("Failed to load any data"))

            val result = loader.processLoading()

            assertTrue(result is LoadResult.Error)
            assertTrue((result as LoadResult.Error).cause is IllegalArgumentException)
        }
    }

    @Test
    fun shouldReturnErrorResult_whenApiReturnedResultWithError() {
        runBlocking {
            whenever(newsApi.getNews(any(), any(), any(), any())).thenReturn(
                NewsDto(
                    status = "error", code = "Something is wrong", message =
                    "Something is wrong error message", totalResults = null, articles = listOf()
                )
            )

            val result = loader.processLoading()

            assertTrue(result is LoadResult.Error)
            assertTrue((result as LoadResult.Error).cause is IllegalStateException)
        }
    }

    private fun Article.matches(dto: ArticleDto): Boolean {
        return sourceName == dto.source.name && author == dto.author && title == dto.title
                && description == dto.description && url == dto.url && imageUrl == dto.imageUrl
    }
}