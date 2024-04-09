package com.ho8278.data.repository

import com.ho8278.data.model.Card
import com.ho8278.data.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface MarbleRepository {
    suspend fun search(nameStartsWith: String, offset: Int): SearchResult
    suspend fun getFavorites(): List<Card>
    suspend fun setFavorite(card: Card)
    suspend fun removeFavorite(id: Int)
    fun favoriteChanges(): Flow<List<Card>>
}