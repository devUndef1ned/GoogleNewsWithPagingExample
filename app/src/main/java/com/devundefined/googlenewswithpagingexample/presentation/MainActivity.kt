package com.devundefined.googlenewswithpagingexample.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.NewsApplication
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.presentation.adapter.ArticlePagedAdapter
import com.devundefined.googlenewswithpagingexample.presentation.adapter.PagedDataList

class MainActivity : AppCompatActivity(), MainView {

    private val loader: View
        get() = findViewById(R.id.loader)
    private val recyclerView: RecyclerView
        get() = findViewById(R.id.recycler_view)
    private val failedContainer: View
        get() = findViewById(R.id.failed_content)
    private val retryButton: Button
        get() = findViewById(R.id.retry_button)

    private val presenter = NewsApplication.INSTANCE.appComponent.mainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val columnsCount = getColumnsCount(resources.configuration.orientation)
        val layoutManager = GridLayoutManager(this, columnsCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 7 == 0) {
                    columnsCount
                } else {
                    1
                }
            }
        }
        recyclerView.layoutManager = layoutManager
        retryButton.setOnClickListener {
            recyclerView.visibility = View.GONE
            loader.visibility = View.VISIBLE
            failedContainer.visibility = View.GONE
            presenter.loadInitial()
        }
    }

    override fun onStart() {
        super.onStart()
        recyclerView.visibility = View.GONE
        loader.visibility = View.VISIBLE
        failedContainer.visibility = View.GONE
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.attachView(this)
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun showData(pagedList: PagedDataList<Article>) {
        recyclerView.adapter = ArticlePagedAdapter(pagedList, { presenter.loadNext() }) {
            openArticleScreen(url, title)
        }
        recyclerView.visibility = View.VISIBLE
        loader.visibility = View.GONE
        failedContainer.visibility = View.GONE
    }

    private fun openArticleScreen(url: String, title: String) {
        Intent(this, WebPageActivity::class.java).apply {
            putExtra(WebPageActivity.EXTRA_KEY_URL, url)
            putExtra(WebPageActivity.EXTRA_KEY_TITLE, title)
        }.run { startActivity(this) }
    }

    override fun showError() {
        recyclerView.visibility = View.GONE
        loader.visibility = View.GONE
        failedContainer.visibility = View.VISIBLE
    }

    private fun getColumnsCount(orientation: Int): Int {
        return when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 3
            else -> throw IllegalArgumentException("No others configurations")
        }
    }
}
