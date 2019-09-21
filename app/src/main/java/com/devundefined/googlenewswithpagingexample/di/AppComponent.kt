package com.devundefined.googlenewswithpagingexample.di

import com.devundefined.googlenewswithpagingexample.di.modules.DomainModule
import com.devundefined.googlenewswithpagingexample.di.modules.InfrastructureModule
import com.devundefined.googlenewswithpagingexample.domain.ArticleLoader
import com.devundefined.googlenewswithpagingexample.infrastructure.backend.NewsApi
import dagger.Component
import javax.inject.Singleton

@Component(modules = [InfrastructureModule::class, DomainModule::class])
@Singleton
interface AppComponent {
    fun articleLoader(): ArticleLoader
}