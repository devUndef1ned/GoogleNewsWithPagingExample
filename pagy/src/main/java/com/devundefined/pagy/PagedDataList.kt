package com.devundefined.pagy

import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

internal class PagedDataList<T : Any>(private val list: MutableList<T> = mutableListOf()) : List<T> by list, PagedList<T> {

    companion object {
        private const val NOT_INIT_SIZE = -1
    }

    private var totalSize: Int = NOT_INIT_SIZE

    private var adapterRef: WeakReference<RecyclerView.Adapter<*>>? = null

    val isFinished
        get() = list.size == totalSize

    override var loadTaskState = LoadTaskState.IDLE
        private set

    override fun setSize(totalSize: Int) {
        this.totalSize = totalSize
    }

    override fun addElements(newElements: Collection<T>) {
        val oldListSize = list.size
        list.addAll(newElements)
        loadTaskState = LoadTaskState.IDLE
        adapterRef?.get()?.notifyItemRemoved(oldListSize)
        adapterRef?.get()?.notifyItemRangeInserted(oldListSize, newElements.size + if (isFinished) 0 else 1)
    }


    override fun changeTaskState(newState: LoadTaskState) {
        loadTaskState = newState
        adapterRef?.get()?.notifyItemChanged(list.size)
    }

    fun attachTo(adapter: RecyclerView.Adapter<*>) {
        adapterRef = WeakReference(adapter)
    }
}