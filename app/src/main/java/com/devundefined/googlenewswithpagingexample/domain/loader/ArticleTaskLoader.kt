package com.devundefined.googlenewswithpagingexample.domain.loader

interface ArticleTaskLoader {

    companion object {
        const val DEFAULT_SIZE_PER_PAGE = 21
    }

    fun load(): ArticleLoadTaskResult
}

class ArticleTaskLoaderImpl(private val articleLoaderProcessor: ArticleLoaderProcessor) : ArticleTaskLoader {

    override fun load(): ArticleLoadTaskResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}