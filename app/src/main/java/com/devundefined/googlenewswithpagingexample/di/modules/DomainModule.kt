package com.devundefined.googlenewswithpagingexample.di.modules

import com.devundefined.googlenewswithpagingexample.domain.loader.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.ArticleProvider
import com.devundefined.googlenewswithpagingexample.domain.ArticleProviderImpl
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository
import com.devundefined.googlenewswithpagingexample.domain.repository.CacheValidator
import com.devundefined.googlenewswithpagingexample.domain.repository.CacheValidatorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideArticleLoader(
        articleLoadProcessor: ArticleLoadProcessor, articleRepository: ArticleRepository,
        cacheValidator: CacheValidator
    ): ArticleProvider = ArticleProviderImpl(articleLoadProcessor, articleRepository, cacheValidator)

    @Provides
    @Singleton
    fun provideCacheValidator(): CacheValidator = CacheValidatorImpl()
}