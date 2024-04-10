package com.ho8278.bookfinder.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho8278.core.error.stable
import com.ho8278.data.model.SearchResult
import com.ho8278.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val marbleRepository: ImageRepository
) : ViewModel() {

    private var isInitialize = false

    private val searchResult = MutableStateFlow<SearchResult?>(null)
    private val searchText = MutableStateFlow("")

    val itemList: Flow<List<ItemHolder>> = searchResult.combine(
        marbleRepository.favoriteChanges()
    ) { search, favorites ->
        search?.results?.map {
            val isFavorite = favorites.contains(it)
            ItemHolder(it, isFavorite)
        } ?: emptyList()
    }

    private val isLoadingLocal = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = isLoadingLocal

    fun init() {
        if (isInitialize) return
        isInitialize = true

        viewModelScope.launch {
            searchText
                .filter { it.length >= 2 }
                .mapLatest {
                    isLoadingLocal.emit(true)
                    val result = marbleRepository.search(it, 0)
                    isLoadingLocal.emit(false)
                    result
                }
                .stable()
                .collect { searchResult.emit(it) }
        }
    }

    fun onTextChanges(query: String) {
        viewModelScope.launch {
            searchText.emit(query)
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (searchResult.value == null) return@launch

            val isLoadAll = searchResult.value!!.total <= searchResult.value!!.offset + OFFSET_COUNT
            if (searchText.value.isEmpty() || isLoadingLocal.value || isLoadAll) {
                return@launch
            }

            isLoadingLocal.emit(true)

            val nextOffset = searchResult.value!!.offset + OFFSET_COUNT
            val loadResult = marbleRepository.search(searchText.value, nextOffset)

            val addedCardList =
                (searchResult.value!!.results + loadResult.results).distinctBy { it.characterId }

            val newSearchResult = SearchResult(loadResult.total, loadResult.offset, addedCardList)
            searchResult.emit(newSearchResult)

            isLoadingLocal.emit(false)
        }
    }

    fun onCardClick(card: Card) {
        viewModelScope.launch {
            val currentFavorite = withContext(Dispatchers.IO) {
                marbleRepository.getFavorites()
            }

            if (currentFavorite.contains(card)) {
                marbleRepository.removeFavorite(card.characterId)
            } else {
                marbleRepository.setFavorite(card)
            }
        }
    }

    companion object {
        private const val OFFSET_COUNT = 10
    }
}