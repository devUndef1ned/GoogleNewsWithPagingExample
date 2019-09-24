package com.devundefined.googlenewswithpagingexample.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devundefined.googlenewswithpagingexample.R
import com.devundefined.googlenewswithpagingexample.domain.Article

class ArticlePagedAdapter(pagedDataList: PagedDataList<Article>, loadAction: () -> Unit) :
    PagedAdapter<Article>(pagedDataList, loadAction) {

    companion object {
        private const val VIEW_TYPE_CONTENT = 2
    }

    override fun getContentItemViewType(position: Int) = VIEW_TYPE_CONTENT

    override fun onCreateLoadTaskStateViewHolder(parent: ViewGroup): PagedViewHolder {
        return LoadTaskStateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_loader_layout,
                parent,
                false
            )
        )
    }

    override fun onCreateContentViewHolder(parent: ViewGroup, viewType: Int): PagedViewHolder {
        return when(viewType) {
            VIEW_TYPE_CONTENT -> ContentViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_article_layout,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("There is not any others type of views")
        }
    }

    override fun onBindContentViewHolder(holder: ContentViewHolder, position: Int) {
        val article = pagedDataList[position]
        holder.setTitle(article.title)
        holder.showImage(article.imageUrl)
        holder.setDate(article.date)
        holder.setDescription(article.description)
        holder.setSource(article.sourceName)
    }

    override fun onBindLoadTaskStateViewHolder(holder: LoadTaskStateViewHolder) {
        holder.setLoadTaskState(pagedDataList.loadTaskState)
        holder.setReloadAction(loadAction)
    }
}