package com.devundefined.googlenewswithpagingexample.di.modules

import com.devundefined.googlenewswithpagingexample.domain.ArticleLoadProcessor
import com.devundefined.googlenewswithpagingexample.domain.ArticleLoader
import com.devundefined.googlenewswithpagingexample.domain.ArticleLoaderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideArticleLoader(articleLoadProcessor: ArticleLoadProcessor): ArticleLoader = ArticleLoaderImpl(articleLoadProcessor)
}