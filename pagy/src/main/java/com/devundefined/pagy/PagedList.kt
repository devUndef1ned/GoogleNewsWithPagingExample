package com.devundefined.pagy

interface PagedList<T : Any> {
    val loadTaskState: LoadTaskState

    fun setSize(totalSize: Int)
    fun addElements(newElements: Collection<T>)
    fun changeTaskState(newState: LoadTaskState)
}