package com.devundefined.googlenewswithpagingexample.domain

import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.loader.LoadResult
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository
import com.devundefined.googlenewswithpagingexample.domain.repository.CacheValidator
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ArticleProviderTests {

    @Mock
    lateinit var loadProcessor: ArticleLoadProcessor
    @Mock
    lateinit var repository: ArticleRepository
    @Mock
    lateinit var cacheValidator: CacheValidator

    private val articleProvider: ArticleProvider by lazy {
        ArticleProviderImpl(
            loadProcessor,
            repository,
            cacheValidator
        )
    }

    @Test
    fun whenGetInitial_shouldReturnPagedData_thatMatchesReceivedFromLoadProcessorData_ifCacheIsNotValid() {
        val article1 =
            Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article1.date.time + 1000)
        )
        val articles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(any(), any())).thenReturn(
            LoadResult.Data(
                articles,
                totalCount = 40,
                countPerPage = 2
            )
        )
        whenever(repository.getTimestampForPage(any(), any())).thenReturn(0)
        whenever(cacheValidator.isValid(any())).thenReturn(false)

        val result = articleProvider.getInitial()

        assertTrue(result is ArticlePageResult.PagedData)
        assertEquals(Source.NETWORK, (result as ArticlePageResult.PagedData).source)
        assertEquals(articles, result.data)
        verify(repository, never()).getPage(any(), any())
    }

    @Test
    fun whenGetInitial_shouldReturnPagedData_thatMatchesReceivedFromRepositoryData_ifCacheIsValid() {
        val article1 =
            Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article1.date.time + 1000)
        )
        val articles = listOf(article1, article2)
        val savedPagedResult = ArticlePageResult.PagedData(articles, 4, Source.LOCAL, 2, 1)
        whenever(repository.getPage(any(), any())).thenReturn(savedPagedResult)
        whenever(repository.getTimestampForPage(any(), any())).thenReturn(1000)
        whenever(cacheValidator.isValid(any())).thenReturn(true)

        val result = articleProvider.getInitial()

        assertTrue(result is ArticlePageResult.PagedData)
        assertEquals(Source.LOCAL, (result as ArticlePageResult.PagedData).source)
        assertEquals(articles, result.data)
        verify(loadProcessor, never()).processLoading(any(), any())
    }

    @Test
    fun whenGetInitial_shouldReturnErrorResult_withCauseThatMatchesErrorCauseFromLoadProcessorData() {
        whenever(loadProcessor.processLoading(any(), any())).thenReturn(
            LoadResult.Error(
                InterruptedException()
            )
        )
        whenever(repository.getTimestampForPage(any(), any())).thenReturn(0)
        whenever(cacheValidator.isValid(any())).thenReturn(false)

        val result = articleProvider.getInitial()

        assertTrue(result is ArticlePageResult.Error)
        assertTrue((result as ArticlePageResult.Error).cause is InterruptedException)
    }

    @Test
    fun whenLoadMore_shouldReturnNewPagedData_thatMatchesReceivedFromLoadProcessorData_ifCacheForNextPageIsNotValid() {
        val pageSize = 2
        val article1 =
            Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article1.date.time + 1000)
        )
        val firstArticles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(eq(1), any())).thenReturn(
            LoadResult.Data(
                firstArticles,
                totalCount = 40,
                countPerPage = pageSize
            )
        )
        val article3 =
            Article("sourceName3", "author3", "title3", "description3", "url3", "imageUrl3", Date())
        val article4 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article3.date.time + 1000)
        )
        val nextArticles = listOf(article3, article4)
        whenever(
            repository.getTimestampForPage(
                pageNumber = 2,
                pageSize = pageSize
            )
        ).thenReturn(100)
        whenever(cacheValidator.isValid(100)).thenReturn(false)
        whenever(
            loadProcessor.processLoading(
                eq(2),
                any()
            )
        ).thenReturn(LoadResult.Data(nextArticles, totalCount = 40, countPerPage = pageSize))
        val firstResult = articleProvider.getInitial()

        val checkingResult = articleProvider.getMore(firstResult as ArticlePageResult.PagedData)

        assertTrue(checkingResult is ArticlePageResult.PagedData)
        assertEquals(nextArticles, (checkingResult as ArticlePageResult.PagedData).data)
        assertEquals(Source.NETWORK, checkingResult.source)
        verify(repository, never()).getPage(eq(2), any())
    }

    @Test
    fun whenLoadMore_shouldReturnNewPagedData_thatMatchesReceivedFromRepository_ifCacheForNextPageIsValid() {
        val pageSize = 2
        val article1 =
            Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article1.date.time + 1000)
        )
        val firstArticles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(eq(1), any())).thenReturn(
            LoadResult.Data(
                firstArticles,
                totalCount = 40,
                countPerPage = pageSize
            )
        )
        val article3 =
            Article("sourceName3", "author3", "title3", "description3", "url3", "imageUrl3", Date())
        val article4 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article3.date.time + 1000)
        )
        val nextArticles = listOf(article3, article4)
        whenever(
            repository.getTimestampForPage(
                pageNumber = 2,
                pageSize = pageSize
            )
        ).thenReturn(100)
        whenever(cacheValidator.isValid(100)).thenReturn(true)
        whenever(repository.getPage(eq(2), any())).thenReturn(
            ArticlePageResult.PagedData(
                nextArticles,
                totalCount = 40,
                source = Source.LOCAL,
                currentPage = 2,
                pageSize = pageSize
            )
        )
        val firstResult = articleProvider.getInitial()

        val checkingResult = articleProvider.getMore(firstResult as ArticlePageResult.PagedData)

        assertTrue(checkingResult is ArticlePageResult.PagedData)
        assertEquals(nextArticles, (checkingResult as ArticlePageResult.PagedData).data)
        assertEquals(Source.LOCAL, checkingResult.source)
        verify(loadProcessor, never()).processLoading(eq(2), any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLoadMore_shouldThrowIllegalArgumentException_forFinishedData() {
        val article1 =
            Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article(
            "sourceName2",
            "author2",
            "title2",
            "description2",
            "url2",
            "imageUrl2",
            Date(article1.date.time + 1000)
        )
        val firstArticles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(any(), any())).thenReturn(
            LoadResult.Data(
                firstArticles,
                totalCount = 2,
                countPerPage = 2
            )
        )
        val firstResult = articleProvider.getInitial()

        articleProvider.getMore(firstResult as ArticlePageResult.PagedData)
    }
}