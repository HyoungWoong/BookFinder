package com.ho8278.bookfinder.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho8278.bookfinder.common.ItemHolder
import com.ho8278.core.error.stable
import com.ho8278.data.model.Image
import com.ho8278.data.model.SearchResult
import com.ho8278.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var isInitialize = false

    private val isLoadingLocal = MutableStateFlow(false)
    private val isLoadMore = AtomicBoolean(false)
    private val searchResult = MutableStateFlow<SearchResult?>(null)
    val searchText = MutableStateFlow("")

    val uiState = combine(
        searchResult,
        imageRepository.favoriteChanges(),
        isLoadingLocal,
    ) { search, favorites, isLoading ->
        val itemList = search?.results?.map {
            val isFavorite = favorites.contains(it)
            ItemHolder(it, isFavorite)
        } ?: emptyList()

        val isEnd = search?.isEnd ?: true

        SearchUiState(itemList, isLoading, isEnd)
    }

    private val isLoadingLocal = MutableStateFlow(false)

    fun init() {
        if (isInitialize) return
        isInitialize = true

        viewModelScope.launch {
            searchText.debounce(DEBOUNCE_TIMEOUT)
                .mapLatest {
                    if (it.isEmpty()) {
                        null
                    } else {
                        isLoadingLocal.emit(true)
                        val result = imageRepository.searchImages(it, DEFAULT_QUERY_PAGE)
                        isLoadingLocal.emit(false)
                        result
                    }
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
            println("Load More!")
            val isNullValue = searchResult.value == null
            val isLoadAll = searchResult.value?.isEnd ?: true
            val isQueryEmpty = searchText.value.isEmpty()
            val isLoading = isLoadMore.get()

            if (isNullValue || isLoadAll || isQueryEmpty || isLoading) {
                return@launch
            }

            isLoadMore.set(true)

            val currentResult = searchResult.value!!

            val nextPage = currentResult.page + 1
            val nextResult = withContext(Dispatchers.IO) {
                imageRepository.searchImages(searchText.value, nextPage)
            }

            val loadMoreList = currentResult.results + nextResult.results
            val loadMoreResult = SearchResult(
                nextResult.total,
                nextResult.isEnd,
                nextResult.page,
                loadMoreList
            )

            searchResult.emit(loadMoreResult)
            isLoadMore.set(false)
        }
    }

    fun addFavorite(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.setFavorite(image)
        }
    }

    fun removeFavorite(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.removeFavorite(image)
        }
    }

    companion object {
        private const val DEFAULT_QUERY_PAGE = 1
        private const val DEBOUNCE_TIMEOUT = 1000L
    }
}