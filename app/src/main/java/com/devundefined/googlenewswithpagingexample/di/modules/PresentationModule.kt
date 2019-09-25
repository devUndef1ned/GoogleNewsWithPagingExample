package com.devundefined.googlenewswithpagingexample.di.modules

import com.devundefined.googlenewswithpagingexample.domain.ArticleProvider
import com.devundefined.googlenewswithpagingexample.presentation.MainPresenter
import com.devundefined.googlenewswithpagingexample.presentation.MainPresenterImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PresentationModule {

    @Provides
    @Singleton
    fun providePresenter(articleProvider: ArticleProvider): MainPresenter = MainPresenterImpl(articleProvider)
}