package com.devundefined.googlenewswithpagingexample.presentation

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import com.devundefined.googlenewswithpagingexample.R

class WebPageActivity : Activity() {
    companion object {
        const val EXTRA_KEY_URL = "extra_key_url"
        const val EXTRA_KEY_TITLE = "extra_key_title"
    }

    private val toolbar: Toolbar
        get() = findViewById(R.id.toolbar)
    private val progressBar: ProgressBar
        get() = findViewById(R.id.loader)
    private val webView: WebView
        get() = findViewById(R.id.webview)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)

        val url = intent.getStringExtra(EXTRA_KEY_URL)
        val title = intent.getStringExtra(EXTRA_KEY_TITLE)

        toolbar.title = title
        toolbar.setNavigationOnClickListener { onBackPressed() }

        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }
    }
}