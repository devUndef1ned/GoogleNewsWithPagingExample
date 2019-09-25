package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import com.devundefined.googlenewswithpagingexample.domain.ArticlePageResult
import com.devundefined.googlenewswithpagingexample.domain.repository.ArticleRepository

class ArticleRepositoryImpl(private val dao: ArticleDao) : ArticleRepository {

    override fun getPage(pageNumber: Int, pageSize: Int): ArticlePageResult.PagedData {
        return find(pageNumber, pageSize)?.let(pageToModel)
            ?: throw IllegalStateException("Do not contain such page number $pageNumber, size $pageSize")
    }

    override fun savePage(pagedData: ArticlePageResult.PagedData): ArticlePageResult.PagedData {
        pagedData.let(pageToEntity).also { entity -> dao.save(entity) }
        return pagedData
    }

    private fun find(pageNumber: Int, pageSize: Int): ArticlePageEntity? = dao.findById(getId(pageNumber, pageSize))

    override fun getTimestampForPage(pageNumber: Int, pageSize: Int): Long {
        return find(pageNumber, pageSize)?.timeStamp?: 0
    }

    override fun clearData(pageNumber: Int, pageSize: Int) {
        find(pageNumber, pageSize)?.also { entity -> dao.delete(entity) }
    }
}