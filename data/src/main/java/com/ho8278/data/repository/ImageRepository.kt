package com.ho8278.data.repository

import com.ho8278.data.model.Image
import com.ho8278.data.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun searchImages(query: String, page: Int): SearchResult
    suspend fun getFavorites(): List<Image>
    suspend fun setFavorite(image: Image)
    suspend fun removeFavorite(image: Image)
    fun favoriteChanges(): Flow<List<Image>>
}