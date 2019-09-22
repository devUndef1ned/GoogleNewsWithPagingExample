package com.devundefined.googlenewswithpagingexample.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.BuildConfig
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.di.AppComponent
import com.devundefined.googlenewswithpagingexample.di.DaggerAppComponent
import com.devundefined.googlenewswithpagingexample.di.modules.InfrastructureModule
import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.presentation.adapter.ArticlePagedAdapter
import com.devundefined.googlenewswithpagingexample.presentation.adapter.PageLoadController
import com.devundefined.googlenewswithpagingexample.presentation.adapter.PagedDataList

class MainActivity : AppCompatActivity(), MainView {

    private val loader: View
        get() = findViewById(R.id.loader)
    private val recyclerView: RecyclerView
        get() = findViewById(R.id.recycler_view)

    private val presenter: MainPresenter by lazy { createPresenter() }
    private val appComponent: AppComponent by lazy { buildAppComponent() }

    private fun buildAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .infrastructureModule(InfrastructureModule(BuildConfig.NewsApiSecretKey))
            .build()
    }

    private fun createPresenter(): MainPresenter = MainPresenterImpl(appComponent.articleLoader())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        recyclerView.visibility = View.GONE
        loader.visibility = View.VISIBLE
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.attachView(this)
        super.onStop()
    }

    override fun showData(pagedList: PagedDataList<Article>) {
        recyclerView.adapter =
            ArticlePagedAdapter(pagedList, object : PageLoadController {
                override fun loadNext() {
                    presenter.loadNext()
                }
            }) { presenter.loadNext() }
        recyclerView.visibility = View.VISIBLE
        loader.visibility = View.GONE
    }

    override fun showError() {

    }
}
