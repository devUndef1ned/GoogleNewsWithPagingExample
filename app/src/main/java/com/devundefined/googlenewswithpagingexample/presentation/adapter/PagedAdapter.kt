package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class PagedAdapter<T : Any>(
    protected val pagedDataList: PagedDataList<T>,
    protected val loadAction: () -> Unit
) : RecyclerView.Adapter<PagedViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LOAD_TASK_STATE = 1
        private const val LOAD_OFFSET = 3
    }

    protected var recyclerView: RecyclerView? = null

    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastVisibleIndex = findLastVisibleChild()
                val totalChildCount = itemCount - 1
                if (needToStartLoading(totalChildCount - lastVisibleIndex)) {
                    loadAction()
                }

            }

            private fun needToStartLoading(endListOffset: Int): Boolean {
                return !pagedDataList.isFinished && pagedDataList.loadTaskState == LoadTaskState.IDLE && endListOffset <= LOAD_OFFSET
            }
        }

    init {
        @Suppress("LeakingThis")
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
        return if (!pagedDataList.isFinished && position >= pagedDataList.size) {
            VIEW_TYPE_LOAD_TASK_STATE
        } else {
            return getContentItemViewType(position)
        }
    }

    abstract fun getContentItemViewType(position: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagedViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOAD_TASK_STATE -> onCreateLoadTaskStateViewHolder(parent)
            else -> onCreateContentViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateLoadTaskStateViewHolder(parent: ViewGroup): PagedViewHolder
    abstract fun onCreateContentViewHolder(parent: ViewGroup, viewType: Int): PagedViewHolder
    abstract fun onBindContentViewHolder(holder: ContentViewHolder, position: Int)
    abstract fun onBindLoadTaskStateViewHolder(holder: LoadTaskStateViewHolder)

    override fun onBindViewHolder(holder: PagedViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> onBindContentViewHolder(holder, position)
            is LoadTaskStateViewHolder -> onBindLoadTaskStateViewHolder(holder)
        }
    }

    override fun onBindViewHolder(
        holder: PagedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder, position)
    }
}

open class PagedViewHolder(view: View) : RecyclerView.ViewHolder(view)