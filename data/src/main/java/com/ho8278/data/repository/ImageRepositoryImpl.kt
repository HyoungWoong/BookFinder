package com.ho8278.data.repository

import com.ho8278.data.BuildConfig
import com.ho8278.data.local.FavoritePref
import com.ho8278.data.model.Favorites
import com.ho8278.data.model.Image
import com.ho8278.data.model.SearchResult
import com.ho8278.data.remote.service.ImageSearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class ImageRepositoryImpl(
    private val imageSearchService: ImageSearchService,
    private val favoritePref: FavoritePref,
) : ImageRepository {

    private val favoriteChangesEvent = MutableSharedFlow<List<Image>>()

    override suspend fun searchImages(query: String, page: Int): SearchResult {
        val searchResult = withContext(Dispatchers.IO) {
            imageSearchService.searchImages(query, page = page)
        }

        val metaData = searchResult.meta
        val totalCount = metaData.totalCount
        val isEnd = metaData.isEnd
        val images = searchResult.documents.map { Image(it.thumbnailUrl) }

        return SearchResult(totalCount, isEnd, page, images)
    }

    override suspend fun getFavorites(): List<Image> {
        val favorites = withContext(Dispatchers.IO) {
            favoritePref.getValue<Favorites>(KEY_FAVORITE, Favorites::class.java)
        }
        return favorites?.images.orEmpty()
    }

    override suspend fun setFavorite(image: Image) {
        val favoriteIds = (getFavorites() + image).toSet()
        val newFavorites = Favorites(favoriteIds.toList())

        favoritePref.putValue(KEY_FAVORITE, newFavorites, Favorites::class.java)
        favoriteChangesEvent.emit(newFavorites.images)
    }

    override suspend fun removeFavorite(image: Image) {
        val removedCardList = getFavorites().toMutableList().apply {
            val position = indexOfFirst { it.thumbnailUrl == image.thumbnailUrl }
            removeAt(position)
        }
        val newFavorites = Favorites(removedCardList)

        favoritePref.putValue(KEY_FAVORITE, newFavorites, Favorites::class.java)
        favoriteChangesEvent.emit(removedCardList)
    }

    override fun favoriteChanges(): Flow<List<Image>> {
        return favoriteChangesEvent.onStart { emit(getFavorites()) }
    }

    companion object {
        private const val KEY_FAVORITE = "key_favorite"
    }
}