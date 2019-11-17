package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.pagy.PagedDataList

interface MainView {
    fun showData(pagedList: PagedDataList<Article>)
    fun showError()
}