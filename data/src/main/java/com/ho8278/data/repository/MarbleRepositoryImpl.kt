package com.ho8278.data.repository

import com.ho8278.data.BuildConfig
import com.ho8278.data.local.FavoritePref
import com.ho8278.data.model.Card
import com.ho8278.data.model.Favorites
import com.ho8278.data.model.SearchResult
import com.ho8278.data.remote.model.toUrl
import com.ho8278.data.remote.service.MarbleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import java.security.MessageDigest

class MarbleRepositoryImpl(
    private val marbleService: MarbleService,
    private val favoritePref: FavoritePref,
) : MarbleRepository {

    private val favoriteChangesEvent = MutableSharedFlow<List<Card>>()

    override suspend fun search(nameStartsWith: String, offset: Int): SearchResult {
        if (nameStartsWith.isEmpty()) return SearchResult(0, 0, emptyList())

        val timestamp = System.currentTimeMillis()
        val hash = getHash(timestamp)
        val characterResult = marbleService.getCharacters(nameStartsWith, hash, timestamp, offset)
        val resultTotal = characterResult.data?.total ?: 0
        val resultOffset = characterResult.data?.offset ?: 0
        val characters = characterResult.data?.results.orEmpty()
            .filter { it.id != null }
            .map {
                Card(
                    it.id!!,
                    it.thumbnail?.toUrl().orEmpty(),
                    it.name.orEmpty(),
                    it.description.orEmpty(),
                )
            }

        return SearchResult(resultTotal, resultOffset, characters)
    }

    override suspend fun getFavorites(): List<Card> {
        val favorites = favoritePref.getValue<Favorites>(KEY_FAVORITE, Favorites::class.java)
        return favorites?.cards.orEmpty()
    }

    override suspend fun setFavorite(card: Card) {
        val favoriteIds = (getFavorites() + card).toSet()

        val adjustedIds: List<Card> = if (favoriteIds.size > MAX_FAVORITE_COUNT) {
            favoriteIds.toMutableList().apply {
                removeFirst()
            }
        } else {
            favoriteIds.toList()
        }
        val newFavorites = Favorites(adjustedIds)

        favoritePref.putValue(KEY_FAVORITE, newFavorites, Favorites::class.java)
        favoriteChangesEvent.emit(adjustedIds)
    }

    override suspend fun removeFavorite(id: Int) {
        val removedCardList = getFavorites().toMutableList().apply {
            val position = indexOfFirst { it.characterId == id }
            removeAt(position)
        }
        val newFavorites = Favorites(removedCardList)

        favoritePref.putValue(KEY_FAVORITE, newFavorites, Favorites::class.java)
        favoriteChangesEvent.emit(removedCardList)
    }

    override fun favoriteChanges(): Flow<List<Card>> {
        return favoriteChangesEvent.onStart { emit(getFavorites()) }
    }

    private fun getHash(timestamp: Long): String {
        val digestInput = "$timestamp${BuildConfig.PRIVATE_KEY}${BuildConfig.API_KEY}"
        return MessageDigest.getInstance("MD5").digest(digestInput.toByteArray()).toHexString()
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    companion object {
        private const val KEY_FAVORITE = "key_favorite"
        private const val MAX_FAVORITE_COUNT = 5
    }
}