package com.devundefined.googlenewswithpagingexample.infrastructure.backend

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/top-headlines")
    suspend fun getNews(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 21
    ): NewsDto
}