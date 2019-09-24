package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.text.format.DateUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devundefined.googlenewswithpagingexample.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

sealed class ArticlePagedViewHolder(view: View) : PagedViewHolder(view)

class ContentViewHolder(view: View) : ArticlePagedViewHolder(view) {
    private val title: TextView
        get() = itemView.findViewById(R.id.title)
    private val imageView: ImageView
        get() = itemView.findViewById(R.id.image)
    private val dateLabel: TextView
        get() = itemView.findViewById(R.id.date)
    private val description: TextView
        get() = itemView.findViewById(R.id.description)
    private val sourceLabel: TextView
        get() = itemView.findViewById(R.id.source)

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun showImage(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(itemView)
                .load(url)
                .fitCenter()
                .into(imageView)
        }
    }

    fun setDate(date: Date) {
        dateLabel.text = DateUtils.getRelativeTimeSpanString(date.time, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS)
    }

    fun setSource(sourceName: String) {
        sourceLabel.text = itemView.context.getString(R.string.source_from, sourceName)
    }

    fun setDescription(description: String) {
        this.description.text = description
    }
}

class LoadTaskStateViewHolder(view: View) : ArticlePagedViewHolder(view) {
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
