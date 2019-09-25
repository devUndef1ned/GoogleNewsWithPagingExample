package com.devundefined.googlenewswithpagingexample.infrastructure.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@TypeConverters(ArticleEntityConverter::class)
@Database(entities = [ArticlePageEntity::class], version = 1)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun dao(): ArticleDao
}