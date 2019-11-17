package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article
import com.devundefined.pagy.PagedDataList
import com.devundefined.pagy.PagedViewHolder

class ArticlePagedAdapter(pagedDataList: PagedDataList<Article>, loadAction: () -> Unit, private val onContentClick: Article.() -> Unit) :
    com.devundefined.pagy.PagedAdapter<Article>(pagedDataList, loadAction) {

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

    override fun onCreateContentViewHolder(parent: ViewGroup, viewType: Int): PagedViewHolder.ContentViewHolder {
        return when(viewType) {
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

    override fun onBindContentViewHolder(holder: PagedViewHolder.ContentViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            val article = pagedDataList[position]
            holder.setTitle(article.title)
            holder.showImage(article.imageUrl)
            holder.setDate(article.date)
            holder.setDescription(article.description)
            holder.setSource(article.sourceName)
            holder.setClickListener { onContentClick(article) }
        }
    }

    override fun onBindLoadTaskStateViewHolder(holder: PagedViewHolder.LoadTaskStateViewHolder) {
        if (holder is LoadStateViewHolder) {
            holder.setLoadTaskState(pagedDataList.loadTaskState)
            holder.setReloadAction(loadAction)
        }
    }
}