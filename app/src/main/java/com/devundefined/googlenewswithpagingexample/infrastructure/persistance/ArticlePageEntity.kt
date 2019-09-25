package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity
data class ArticlePageEntity(
    @PrimaryKey
    val id: String,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
    val data: List<ArticleEntity>,
    val timeStamp: Long
)

data class ArticleEntity(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val dateTime: Long
)

class ArticleEntityConverter {
    private val type = object : TypeToken<List<ArticleEntity>>() {}.type

    @TypeConverter
    fun fromArticleEntityList(entities: List<ArticleEntity>): String {
        return Gson().toJson(entities, type)
    }

    @TypeConverter
    fun toArticleEntityList(listString: String): List<ArticleEntity> {
        return Gson().fromJson(listString, type)
    }
}