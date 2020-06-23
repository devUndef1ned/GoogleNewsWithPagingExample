package com.devundefined.pagy

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class PagedAdapter<T : Any>(
    private val loadOffset: Int,
    protected val loadAction: () -> Unit
) : RecyclerView.Adapter<PagedViewHolder>(), PagedList<T> {

    companion object {
        private const val VIEW_TYPE_LOAD_TASK_STATE = 1
    }

    private val pagedDataList: PagedDataList<T> = PagedDataList()
    private var recyclerView: RecyclerView? = null

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
                return !pagedDataList.isFinished && pagedDataList.loadTaskState == LoadTaskState.IDLE && endListOffset <= loadOffset
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

    /**
     * Should return Integer according to type of content.
     */
    abstract fun getContentItemViewType(position: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagedViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOAD_TASK_STATE -> onCreateLoadTaskStateViewHolder(parent)
            else -> onCreateContentViewHolder(parent, viewType)
        }
    }

    /**
     * Should create ViewHolder for the LoadTask cell.
     */
    abstract fun onCreateLoadTaskStateViewHolder(parent: ViewGroup): PagedViewHolder.LoadTaskStateViewHolder

    /**
     * Should create ViewHolder for a content cell according to [viewType].
     */
    abstract fun onCreateContentViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagedViewHolder.ContentViewHolder

    /**
     * Binds data to particular content view holder.
     */
    abstract fun onBindContentViewHolder(holder: PagedViewHolder.ContentViewHolder, data: T)

    /**
     * Binds [loadTaskState] to view holder.
     */
    abstract fun onBindLoadTaskStateViewHolder(
        holder: PagedViewHolder.LoadTaskStateViewHolder,
        loadTaskState: LoadTaskState
    )

    override fun onBindViewHolder(holder: PagedViewHolder, position: Int) {
        when (holder) {
            is PagedViewHolder.ContentViewHolder -> onBindContentViewHolder(
                holder,
                pagedDataList[position]
            )
            is PagedViewHolder.LoadTaskStateViewHolder -> onBindLoadTaskStateViewHolder(
                holder,
                pagedDataList.loadTaskState
            )
        }
    }

    override fun onBindViewHolder(
        holder: PagedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder, position)
    }

    override val loadTaskState: LoadTaskState
        get() = pagedDataList.loadTaskState

    override fun setSize(totalSize: Int) = pagedDataList.setSize(totalSize)
    override fun addElements(newElements: Collection<T>) = pagedDataList.addElements(newElements)
    override fun changeTaskState(newState: LoadTaskState) = pagedDataList.changeTaskState(newState)
}

sealed class PagedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract class ContentViewHolder(view: View) : PagedViewHolder(view)
    abstract class LoadTaskStateViewHolder(view: View) : PagedViewHolder(view)
}