package com.devundefined.googlenewswithpagingexample.presentation

interface MainPresenter {
    fun attachView(view: MainView)
    fun detachView()
    fun loadInitial()
    fun loadNext()
    fun onDestroy()
}