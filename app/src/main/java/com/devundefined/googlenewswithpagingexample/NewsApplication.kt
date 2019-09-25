package com.devundefined.googlenewswithpagingexample

import android.app.Application
import com.devundefined.googlenewswithpagingexample.di.AppComponent
import com.devundefined.googlenewswithpagingexample.di.DaggerAppComponent
import com.devundefined.googlenewswithpagingexample.di.modules.InfrastructureModule

class NewsApplication : Application() {

    companion object {
        lateinit var INSTANCE: NewsApplication
            private set
    }

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        buildAppComponent()
    }

    private fun buildAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .infrastructureModule(InfrastructureModule(BuildConfig.NewsApiSecretKey, this))
            .build()
    }
}