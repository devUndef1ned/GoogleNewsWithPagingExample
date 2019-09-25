package com.devundefined.googlenewswithpagingexample.presentation

import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult
import com.devundefined.googlenewswithpagingexample.domain.ArticleProvider
import com.devundefined.googlenewswithpagingexample.presentation.adapter.LoadTaskState
import com.devundefined.googlenewswithpagingexample.presentation.adapter.PagedDataList
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
            mainView?.showData(state.pagedDataList)
        }
    }

    private fun loadInitial() {
        runBlocking {
            job = bgScope.launch {
                when (val loadResult = articleProvider.getInitial()) {
                    is ArticlePageResult.PagedData -> {
                        state =
                            ScreenState.createInitial(loadResult)
                        runInMainThread { mainView?.showData(state.pagedDataList) }
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
            state.pagedDataList.changeTaskState(LoadTaskState.FAILED)
            android.util.Log.e(
                LOG_TAG,
                "$message\n${error}"
            )
        }
    }

    override fun loadNext() {
        state.pagedDataList.changeTaskState(LoadTaskState.LOADING)
        runBlocking {
            job = bgScope.launch {
                try {
                    val newData = articleProvider.getMore(state.currentPage)
                    when (newData) {
                        is ArticlePageResult.PagedData -> runInMainThread {
                            state = state.mutate(newData)
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

class ScreenState {
    companion object {
        fun createInitial(articleLoaderPageResult: ArticlePageResult.PagedData) =
            ScreenState().apply {
                this.pagedDataList = PagedDataList(
                    articleLoaderPageResult.data.toMutableList(),
                    articleLoaderPageResult.totalCount
                )
                this.currentPage = articleLoaderPageResult
            }
        fun create(pagedDataList: PagedDataList<Article>, currentPage: ArticlePageResult.PagedData) =
            ScreenState().apply {
                this.pagedDataList = pagedDataList
                this.currentPage = currentPage
            }
    }

    lateinit var pagedDataList: PagedDataList<Article>
    lateinit var currentPage: ArticlePageResult.PagedData

    fun isInitialized() = this::pagedDataList.isInitialized

    fun mutate(pagedData: ArticlePageResult.PagedData): ScreenState {
        val newPagedDataList = pagedDataList.apply { addElements(pagedData.data) }
        return create(newPagedDataList, pagedData)
    }
}