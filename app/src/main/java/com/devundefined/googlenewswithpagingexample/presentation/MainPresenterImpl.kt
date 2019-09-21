package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.ArticleLoadPageResult
import com.devundefined.googlenewswithpagingexample.domain.ArticleLoader
import com.devundefined.googlenewswithpagingexample.presentation.adapter.LoadTaskState
import com.devundefined.googlenewswithpagingexample.presentation.adapter.PagedDataList
import kotlinx.coroutines.*

class MainPresenterImpl(private val articleLoader: ArticleLoader) : MainPresenter {

    companion object {
        private const val LOG_TAG = "MainPresenter"
    }

    private var mainView: MainView? = null

    private var job: Job? = null
    private val bgScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var state: ScreenState = ScreenState()

    override fun attachView(view: MainView) {
        mainView = view
        if (!state.isInitialized()) {
            loadInitial()
        }
    }

    private fun loadInitial() {
        runBlocking {
            job = bgScope.launch {
                when (val loadResult = articleLoader.load()) {
                    is ArticleLoadPageResult.PagedData -> {
                        state =
                            ScreenState.createInitial(loadResult)
                        runInMainThread { mainView?.showData(state.pagedDataList) }
                    }
                    is ArticleLoadPageResult.Error -> handleError(
                        loadResult.cause,
                        "Failed to load initial Data with exception"
                    )
                }
            }
        }
    }

    override fun detachView() {
        mainView = null
    }

    private fun handleError(error: Throwable, message: String) {
        runInMainThread {
            state.pagedDataList.changeTaskState(LoadTaskState.FAILED)
            android.util.Log.e(
                LOG_TAG,
                "$message\n${error}"
            )
        }
    }

    override fun loadNext() {
        runBlocking {
            state.pagedDataList.changeTaskState(LoadTaskState.LOADING)
            job = bgScope.launch {
                try {
                    val newData = articleLoader.loadMore(state.currentPage)
                    when (newData) {
                        is ArticleLoadPageResult.PagedData -> runInMainThread {
                            state.pagedDataList.addElements(newData.data)
                        }
                        is ArticleLoadPageResult.Error -> handleError(
                            newData.cause,
                            "Failed to load more Data with exception"
                        )
                    }
                } catch (e: Exception) {
                    handleError(e, "Failed to load more Data with exception")
                }
            }
        }
    }

    private fun runInMainThread(task: () -> Unit) {
        uiScope.launch { task() }
    }
}

class ScreenState {
    companion object {
        fun createInitial(articleLoaderPageResult: ArticleLoadPageResult.PagedData) =
            ScreenState().apply {
                this.pagedDataList = PagedDataList(
                    articleLoaderPageResult.data.toMutableList(),
                    articleLoaderPageResult.totalCount
                )
                this.currentPage = articleLoaderPageResult
            }
    }

    lateinit var pagedDataList: PagedDataList<Article>
    lateinit var currentPage: ArticleLoadPageResult.PagedData

    fun isInitialized() = this::pagedDataList.isInitialized
}