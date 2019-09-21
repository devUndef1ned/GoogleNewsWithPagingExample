package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.R

sealed class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view)

class ContentViewHolder(view: View) : ArticleViewHolder(view) {
    private val title: TextView
        get() = itemView.findViewById(R.id.title)

    fun setTitle(title: String) {
        this.title.text = title
    }
}

class NetworkStateViewHolder(view: View) : ArticleViewHolder(view) {
    fun setLoadTaskState(state: LoadTaskState) {

    }
}
