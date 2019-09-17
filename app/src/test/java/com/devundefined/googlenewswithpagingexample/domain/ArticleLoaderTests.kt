package com.devundefined.googlenewswithpagingexample.domain

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ArticleLoaderTests {
    
    @Mock
    lateinit var loadProcessor: ArticleLoadProcessor
    
    private val articleLoader: ArticleLoader by lazy { ArticleLoaderImpl(loadProcessor) }
    
    @Test
    fun whenLoad_shouldReturnPagedData_thatMatchesReceivedFromLoadProcessorData() {
        val article1 = Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article("sourceName2", "author2", "title2", "description2", "url2", "imageUrl2", Date(article1.date.time + 1000))
        val articles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(any())).thenReturn(LoadResult.Data(articles, totalCount = 40, countPerPage = 2))

        val result = articleLoader.load()

        assertTrue(result is ArticleLoadPageResult.PagedData)
        assertEquals(articles, (result as ArticleLoadPageResult.PagedData).data)
    }

    @Test
    fun whenLoad_shouldReturnErrorResult_withCauseThatMatchesErrorCauseFromLoadProcessorData() {
        whenever(loadProcessor.processLoading(any())).thenReturn(LoadResult.Error(InterruptedException()))

        val result = articleLoader.load()

        assertTrue(result is ArticleLoadPageResult.Error)
        assertTrue((result as ArticleLoadPageResult.Error).cause is InterruptedException)
    }

    @Test
    fun whenLoadMore_shouldReturnNewPagedData_thatMatchesReceivedFromLoadProcessorData() {
        val article1 = Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article("sourceName2", "author2", "title2", "description2", "url2", "imageUrl2", Date(article1.date.time + 1000))
        val firstArticles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(1)).thenReturn(LoadResult.Data(firstArticles, totalCount = 40, countPerPage = 2))
        val article3 = Article("sourceName3", "author3", "title3", "description3", "url3", "imageUrl3", Date())
        val article4 = Article("sourceName2", "author2", "title2", "description2", "url2", "imageUrl2", Date(article3.date.time + 1000))
        val nextArticles = listOf(article3, article4)
        whenever(loadProcessor.processLoading(2)).thenReturn(LoadResult.Data(nextArticles, totalCount = 40, countPerPage = 2))
        val firstResult = articleLoader.load()

        val checkingResult = articleLoader.loadMore(firstResult as ArticleLoadPageResult.PagedData)

        assertTrue(checkingResult is ArticleLoadPageResult.PagedData)
        assertEquals(nextArticles, (checkingResult as ArticleLoadPageResult.PagedData).data)
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLoadMore_shouldThrowIllegalArgumentException_forFinishedData() {
        val article1 = Article("sourceName1", "author1", "title1", "description1", "url1", "imageUrl1", Date())
        val article2 = Article("sourceName2", "author2", "title2", "description2", "url2", "imageUrl2", Date(article1.date.time + 1000))
        val firstArticles = listOf(article1, article2)
        whenever(loadProcessor.processLoading(any())).thenReturn(LoadResult.Data(firstArticles, totalCount = 2, countPerPage = 2))
        val firstResult = articleLoader.load()

        articleLoader.loadMore(firstResult as ArticleLoadPageResult.PagedData)
    }
}