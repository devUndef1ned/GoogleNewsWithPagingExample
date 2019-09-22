package com.devundefined.googlenewswithpagingexample.presentation.adapter

import androidx.recyclerview.widget.RecyclerView

class PagedDataList<T : Any>(
    private val list: MutableList<T>,
    private val totalSize: Int
) : List<T> {

    override val size: Int
        get() = list.size

    override fun contains(element: T) = list.contains(element)
    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)
    override fun get(index: Int) = list[index]
    override fun indexOf(element: T) = list.indexOf(element)
    override fun isEmpty() = list.isEmpty()
    override fun iterator() = list.iterator()
    override fun lastIndexOf(element: T) = list.lastIndexOf(element)
    override fun listIterator() = list.listIterator()
    override fun listIterator(index: Int) = list.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)

    var adapter: RecyclerView.Adapter<*>? = null

    val isFinished
        get() = list.size == totalSize

    var loadTaskState = LoadTaskState.IDLE
        private set

    fun addElements(newElements: Collection<T>) {
        val oldListSize = list.size
        list.addAll(newElements)
        loadTaskState = LoadTaskState.IDLE
        adapter?.notifyItemRemoved(oldListSize)
        adapter?.notifyItemRangeInserted(oldListSize, newElements.size + if (isFinished) 0 else 1)
    }


    fun changeTaskState(newState: LoadTaskState) {
        loadTaskState = newState
        adapter?.notifyItemChanged(list.size)
    }

    fun attachTo(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }
}