package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult
import com.devundefined.googlenewswithpagingexample.domain.ArticleProvider
import com.devundefined.pagy.LoadTaskState
import kotlinx.coroutines.*

class MainPresenterImpl(private val articleProvider: ArticleProvider) : MainPresenter {

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
        } else {
            mainView?.showContent()
            mainView?.showPagedData(state.totalSize, state.currentList)
        }
    }

    override fun loadInitial() {
        mainView?.showProgress()
        runBlocking {
            job = bgScope.launch {
                when (val loadResult = articleProvider.getInitial()) {
                    is ArticlePageResult.PagedData -> {
                        state =
                            ScreenState.createInitial(loadResult)
                        runInMainThread {
                            mainView?.showContent()
                            mainView?.showPagedData(state.totalSize, state.currentList)
                        }
                    }
                    is ArticlePageResult.Error -> uiScope.launch {
                        mainView?.showError()
                        android.util.Log.e(
                            LOG_TAG,
                            "Failed to getInitial initial data\n${loadResult.cause}"
                        )
                    }
                }
            }
        }
    }

    override fun detachView() {
        mainView = null
    }

    private fun handleError(error: Throwable, message: String) {
        runInMainThread {
            mainView?.showTaskState(LoadTaskState.FAILED)
            android.util.Log.e(
                LOG_TAG,
                "$message\n${error}"
            )
        }
    }

    override fun loadNext() {
        mainView?.showTaskState(LoadTaskState.LOADING)
        runBlocking {
            job = bgScope.launch {
                try {
                    val newData = articleProvider.getMore(
                        state.currentPage ?: throw IllegalArgumentException("Must be non null!")
                    )
                    when (newData) {
                        is ArticlePageResult.PagedData -> runInMainThread {
                            state = state.mutate(newData)
                            mainView?.showPagedData(
                                state.totalSize,
                                state.currentPage?.data ?: listOf()
                            )
                        }
                        is ArticlePageResult.Error -> handleError(
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

    override fun onDestroy() {
        job?.cancel()
    }
}

