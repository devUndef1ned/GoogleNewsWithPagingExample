package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article
import kotlinx.coroutines.*

class ArticlePagedAdapter(
    private val pagedDataList: PagedDataList<Article>,
    private val diffUtilItemCallback: DiffUtil.ItemCallback<Article>,
    private val pageLoadController: PageLoadController
) :
    RecyclerView.Adapter<ArticleViewHolder>(),
    LoadTaskStateListener,
    PagedDataList.Observer<Article> {

    companion object {
        private const val VIEW_TYPE_CONTENT = 1
        private const val VIEW_TYPE_NETWORK_STATE = 2
        private const val LOAD_OFFSET = 3
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val bgScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var recyclerView: RecyclerView? = null

    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastVisibleIndex = findLastVisibleChild()
                val totalChildCount = itemCount

                if (needToStartLoading(totalChildCount - lastVisibleIndex)) {
                    pageLoadController.loadNext()
                }
            }

            private fun needToStartLoading(endListOffset: Int): Boolean {
                return !pagedDataList.isFinished && pagedDataList.loadTaskState == LoadTaskState.IDLE && endListOffset < LOAD_OFFSET
            }
        }

    init {
        pagedDataList.observer = this
        pagedDataList.setLoadTaskStateListener(this)
    }

    private fun findLastVisibleChild(): Int {
        for (i in 0 until itemCount) {
            val vh = recyclerView?.findViewHolderForAdapterPosition(i)
            if (vh != null && i != 0 && vh.itemView == null) {
                return i - 1
            }
        }
        return itemCount - 1
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
            if (position <= pagedDataList.size) {
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
            is ContentViewHolder -> holder.setTitle(pagedDataList[position].title)
            is NetworkStateViewHolder -> holder.setLoadTaskState(pagedDataList.loadTaskState)
        }
        if (holder is ContentViewHolder) {
            holder.setTitle(pagedDataList[position].title)
        }
    }

    override fun onLoadTaskStateChanged() {
        uiScope.launch {
            val newState = pagedDataList.loadTaskState
            when (newState) {
                LoadTaskState.IDLE -> notifyItemRemoved(pagedDataList.size + 1)
                else -> notifyItemChanged(pagedDataList.size + 1, newState)
            }
        }
    }

    override fun onListChanged(oldList: List<Article>, newList: List<Article>) {
        uiScope.launch {
            val diffResult = bgScope.async {
                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        diffUtilItemCallback.areItemsTheSame(
                            oldList[oldItemPosition],
                            newList[newItemPosition]
                        )

                    override fun getOldListSize() = oldList.size
                    override fun getNewListSize() = newList.size

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ) = diffUtilItemCallback.areContentsTheSame(
                        oldList[oldItemPosition],
                        newList[newItemPosition]
                    )
                })
            }.await()
            diffResult.dispatchUpdatesTo(this@ArticlePagedAdapter)
        }
    }
}


object DiffUtilItemCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
}