package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.pagy.LoadTaskState
import com.devundefined.pagy.PagedAdapter
import com.devundefined.pagy.PagedViewHolder

class ArticlePagedAdapter(
    loadOffset: Int,
    loadAction: () -> Unit,
    private val onContentClick: Article.() -> Unit
) : PagedAdapter<Article>(loadOffset, loadAction) {

    companion object {
        private const val VIEW_TYPE_CONTENT = 2
    }

    override fun getContentItemViewType(position: Int) = VIEW_TYPE_CONTENT

    override fun onCreateLoadTaskStateViewHolder(parent: ViewGroup): PagedViewHolder.LoadTaskStateViewHolder {
        return LoadStateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_loader_layout,
                parent,
                false
            )
        )
    }

    override fun onCreateContentViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagedViewHolder.ContentViewHolder {
        return when (viewType) {
            VIEW_TYPE_CONTENT -> ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_article_layout,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("There is not any others type of views")
        }
    }

    override fun onBindContentViewHolder(holder: PagedViewHolder.ContentViewHolder, data: Article) {
        if (holder is ArticleViewHolder) {
            holder.setTitle(data.title)
            holder.showImage(data.imageUrl)
            holder.setDate(data.date)
            holder.setDescription(data.description)
            holder.setSource(data.sourceName)
            holder.setClickListener { onContentClick(data) }
        }
    }

    override fun onBindLoadTaskStateViewHolder(holder: PagedViewHolder.LoadTaskStateViewHolder, loadTaskState: LoadTaskState) {
        if (holder is LoadStateViewHolder) {
            holder.setLoadTaskState(loadTaskState)
            holder.setReloadAction(loadAction)
        }
    }
}