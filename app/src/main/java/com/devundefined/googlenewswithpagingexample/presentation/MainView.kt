package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.pagy.LoadTaskState

interface MainView {
    fun showData(pagedList: Collection<Article>)
    fun showError()
    fun showTaskState(taskState: LoadTaskState)
    fun showContent()
    fun showProgress()
}