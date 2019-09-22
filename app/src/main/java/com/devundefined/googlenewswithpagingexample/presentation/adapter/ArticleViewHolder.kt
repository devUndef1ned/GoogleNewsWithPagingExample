package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.googlenewswithpagingexample.R

sealed class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view)

class ContentViewHolder(view: View) : ArticleViewHolder(view) {
    private val position: TextView
        get() = itemView.findViewById(R.id.position_label)
    private val title: TextView
        get() = itemView.findViewById(R.id.title)

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun setIndex(index: Int) {
        position.text = "# ${index + 1}"
    }
}

class NetworkStateViewHolder(view: View) : ArticleViewHolder(view) {
    private val loader: ProgressBar
        get() = itemView.findViewById(R.id.loader)
    private val retryButton: Button
        get() = itemView.findViewById(R.id.reload_button)
    private val container: View
        get() = itemView.findViewById(R.id.state_content)

    fun setLoadTaskState(state: LoadTaskState) {
        when (state) {
            LoadTaskState.IDLE -> container.makeGone()
            LoadTaskState.LOADING -> {
                container.makeVisible()
                loader.makeVisible()
                retryButton.makeGone()
            }
            LoadTaskState.FAILED -> {
                container.makeVisible()
                loader.makeGone()
                retryButton.makeVisible()
            }
        }
    }

    fun setReloadAction(reloadAction: () -> Unit) {
        retryButton.setOnClickListener { reloadAction() }
    }

    fun View.makeVisible() {
        visibility = View.VISIBLE
    }

    fun View.makeGone() {
        visibility = View.GONE
    }
}
