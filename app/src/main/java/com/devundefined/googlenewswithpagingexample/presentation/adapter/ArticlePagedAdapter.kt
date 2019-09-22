package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article

class ArticlePagedAdapter(
    private val pagedDataList: PagedDataList<Article>,
    private val pageLoadController: PageLoadController,
    private val reloadAction: () -> Unit
) : RecyclerView.Adapter<ArticleViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 1
        private const val VIEW_TYPE_NETWORK_STATE = 2
        private const val LOAD_OFFSET = 3
    }

    private var recyclerView: RecyclerView? = null

    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastVisibleIndex = findLastVisibleChild()
                val totalChildCount = itemCount - 1
                if (needToStartLoading(totalChildCount - lastVisibleIndex)) {
                    pageLoadController.loadNext()
                }

            }

            private fun needToStartLoading(endListOffset: Int): Boolean {
                return !pagedDataList.isFinished && pagedDataList.loadTaskState == LoadTaskState.IDLE && endListOffset <= LOAD_OFFSET
            }
        }

    init {
        pagedDataList.attachTo(this)
    }

    private fun findLastVisibleChild(): Int {
        for (i in (itemCount - 1) downTo 0) {
            val vh = recyclerView?.findViewHolderForAdapterPosition(i)
            if (vh != null && i != 0) {
                return i
            }
        }
        return 0
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        this.recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView?.removeOnScrollListener(scrollListener)
        this.recyclerView = null
    }

    override fun getItemCount() = pagedDataList.size + if (pagedDataList.isFinished) 0 else 1

    override fun getItemViewType(position: Int): Int {
        return if (pagedDataList.isFinished) {
            VIEW_TYPE_CONTENT
        } else {
            if (position < pagedDataList.size) {
                VIEW_TYPE_CONTENT
            } else {
                VIEW_TYPE_NETWORK_STATE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return when (viewType) {
            VIEW_TYPE_CONTENT -> ContentViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_article_layout,
                    parent,
                    false
                )
            )
            VIEW_TYPE_NETWORK_STATE -> NetworkStateViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loader_layout,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("No other view type")
        }

    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> bindContent(holder, position)
            is NetworkStateViewHolder -> bindNetworkState(holder)
        }
        if (holder is ContentViewHolder) {
            holder.setTitle(pagedDataList[position].title)
        }
    }

    private fun bindContent(holder: ContentViewHolder, position: Int) {
        val article = pagedDataList[position]
        holder.setTitle(article.title)
        holder.setIndex(position)
        holder.showImage(article.imageUrl)
        holder.setDate(article.date)
        holder.setDescription(article.description)
        holder.setSource(article.sourceName)
    }

    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder, position)
    }

    private fun bindNetworkState(holder: NetworkStateViewHolder) {
        holder.setLoadTaskState(pagedDataList.loadTaskState)
        holder.setReloadAction(reloadAction)
    }
}