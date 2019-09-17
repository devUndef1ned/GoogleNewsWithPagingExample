package com.devundefined.googlenewswithpagingexample.domain

interface ArticleLoader {
    fun load(): ArticleLoadPageResult
    @Throws(IllegalArgumentException::class)
    fun loadMore(articleLoaderPageResult: ArticleLoadPageResult.PagedData): ArticleLoadPageResult
}

class ArticleLoaderImpl(private val articleLoadProcessor: ArticleLoadProcessor) :
    ArticleLoader {

    override fun load() = innerLoad()

    @Throws(IllegalArgumentException::class)
    override fun loadMore(articleLoaderPageResult: ArticleLoadPageResult.PagedData): ArticleLoadPageResult {
        if (!articleLoaderPageResult.isFinished) {
            return innerLoad(articleLoaderPageResult.currentPage + 1)
        } else {
            throw IllegalArgumentException("Can not load more for finishedPagedData")
        }
    }

    private fun innerLoad(page: Int = 1): ArticleLoadPageResult {
        val loadResult = articleLoadProcessor.processLoading(page)
        return when (loadResult) {
            is LoadResult.Error -> ArticleLoadPageResult.Error(
                loadResult.cause
            )
            is LoadResult.Data -> ArticleLoadPageResult.PagedData(
                loadResult.pagedArticles,
                loadResult.totalCount,
                loadResult.countPerPage,
                loadResult.currentPage
            )
        }
    }
}