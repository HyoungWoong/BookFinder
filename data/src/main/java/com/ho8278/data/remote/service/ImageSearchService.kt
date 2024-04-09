package com.ho8278.data.remote.service

import com.ho8278.data.remote.model.SearchImagesResult
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageSearchService {
    @GET("v2/search/image")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 80,
    ): SearchImagesResult
}