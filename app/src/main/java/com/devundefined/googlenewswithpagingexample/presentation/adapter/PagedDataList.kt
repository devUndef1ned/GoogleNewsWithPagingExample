package com.devundefined.googlenewswithpagingexample.presentation.adapter

class PagedDataList<T : Any>(private val list: MutableList<T>, private val totalSize: Int) : List<T> {

    interface Observer<T> {
        fun onListChanged(oldList: List<T>, newList: List<T>)
    }

    override val size: Int = list.size
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

    var observer: Observer<T>? = null

    val isFinished 
        get() = list.size == totalSize

    private val networkStateHolder = NetworkStateHolder()
    val loadTaskState = networkStateHolder.state

    fun addElements(newElements: Collection<T>) {
        changeTaskState(LoadTaskState.IDLE)
        val oldList = list.toList()
        list.addAll(newElements)
        val newList = list.toList()
        observer?.onListChanged(oldList, newList)
    }

    fun setLoadTaskStateListener(loadTaskStateListener: LoadTaskStateListener) {
        networkStateHolder.listener = loadTaskStateListener
    }

    fun changeTaskState(newState: LoadTaskState) {
        networkStateHolder.state = newState
    }
}