package com.devundefined.googlenewswithpagingexample.domain

import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.loader.LoadResult
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository
import com.devundefined.googlenewswithpagingexample.domain.repository.CacheValidator

interface ArticleProvider {
    fun getInitial(): ArticlePageResult
    @Throws(IllegalArgumentException::class)
    fun getMore(articlePageResult: ArticlePageResult.PagedData): ArticlePageResult
}

class ArticleProviderImpl(private val articleLoadProcessor: ArticleLoadProcessor,
                          private val articleRepository: ArticleRepository,
                          private val cacheValidator: CacheValidator) : ArticleProvider {

    companion object {
        const val DEFAULT_SIZE_PER_PAGE = 21
    }

    override fun getInitial() = provideData()

    @Throws(IllegalArgumentException::class)
    override fun getMore(articlePageResult: ArticlePageResult.PagedData): ArticlePageResult {
        return if (!articlePageResult.isFinished) {
            provideData(articlePageResult)
        } else {
            throw IllegalArgumentException("Can not provide more for finishedPagedData")
        }
    }

    private fun provideData(articlePageResult: ArticlePageResult.PagedData? = null): ArticlePageResult {
        val pageNumber = (articlePageResult?.currentPage?: 0) + 1
        val pageSize = articlePageResult?.pageSize?: DEFAULT_SIZE_PER_PAGE
        val cacheTimestamp = articleRepository.getTimestampForPage(pageNumber, pageSize)
        return if (loadFromRepositoryCriteria(cacheTimestamp)) {
            articleRepository.getPage(pageNumber, pageSize)
        } else {
            articleRepository.clearData(pageNumber, pageSize)
            loadFromApi(pageNumber, pageSize)
        }
    }

    private fun loadFromRepositoryCriteria(cacheTimestamp: Long): Boolean {
        return cacheValidator.isValid(cacheTimestamp)
    }

    private fun loadFromApi(page: Int, pageSize: Int): ArticlePageResult {
        val loadResult = articleLoadProcessor.processLoading(page, pageSize)
        return when (loadResult) {
            is LoadResult.Error -> ArticlePageResult.Error(
                loadResult.cause
            )
            is LoadResult.Data -> ArticlePageResult.PagedData(
                loadResult.pagedArticles,
                loadResult.totalCount,
                Source.NETWORK,
                loadResult.countPerPage,
                loadResult.currentPage
            ).also { pagedData -> articleRepository.savePage(pagedData) }
        }
    }
}