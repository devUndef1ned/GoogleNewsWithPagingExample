package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArticleDao {
    @Query("SELECT * from ArticlePageEntity where id = :id")
    fun findById(id: String): ArticlePageEntity?

    @Insert
    fun save(entity: ArticlePageEntity)

    @Delete
    fun delete(entity: ArticlePageEntity)
}