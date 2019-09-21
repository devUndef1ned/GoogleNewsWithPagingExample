package com.devundefined.googlenewswithpagingexample.presentation.adapter

import kotlin.properties.Delegates

enum class LoadTaskState {
    IDLE,
    LOADING,
    FAILED
}

interface LoadTaskStateListener {
    fun onLoadTaskStateChanged()
}

class NetworkStateHolder {

    var state by Delegates.observable(LoadTaskState.FAILED) { _, _, _ ->
        listener?.onLoadTaskStateChanged()
    }

    var listener: LoadTaskStateListener? = null
}