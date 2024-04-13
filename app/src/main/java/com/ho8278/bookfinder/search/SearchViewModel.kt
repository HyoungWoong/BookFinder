package com.ho8278.bookfinder.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.common.BaseSignal
import com.ho8278.bookfinder.common.ToastSignal
import com.ho8278.core.error.stable
import com.ho8278.data.model.Image
import com.ho8278.data.model.SearchResult
import com.ho8278.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private var isInitialize = false

    private val isLoadingLocal = MutableStateFlow(false)
    private val isLoadMore = AtomicBoolean(false)
    private val searchResult = MutableStateFlow<SearchResult?>(null)
    private val searchText = MutableStateFlow("")

    val uiState = combine(
        searchResult,
        imageRepository.favoriteChanges()
            .flowOn(Dispatchers.IO),
        isLoadingLocal,
        searchText,
    ) { search, favorites, isLoading, searchText ->
        val itemList = search?.results?.map {
            val isFavorite = favorites.contains(it)
            SearchItemHolder(it, isFavorite)
        } ?: emptyList()

        val isEnd = search?.isEnd ?: true

        SearchUiState(searchText, itemList, isLoading, isEnd)
    }
        .flowOn(Dispatchers.Default)

    private val signalInternal = Channel<BaseSignal>()
    val signals: Flow<BaseSignal> = signalInternal.receiveAsFlow()

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
                        val result = withContext(Dispatchers.IO) {
                            try {
                                imageRepository.searchImages(it, DEFAULT_QUERY_PAGE)
                            } catch (throwable: Throwable) {
                                when (throwable) {
                                    is HttpException,
                                    is UnknownHostException -> {
                                        val signal = ToastSignal(
                                            applicationContext.getString(R.string.network_error_message)
                                        )

                                        signalInternal.send(signal)
                                        null
                                    }

                                    else -> throw throwable
                                }
                            }
                        }
                        isLoadingLocal.emit(false)
                        result
                    }
                }
                .flowOn(Dispatchers.Default)
                .stable()
                .collect { searchResult.emit(it) }
        }
    }

    fun onTextChanges(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            searchText.emit(query)
        }
    }

    fun loadMore() {
        viewModelScope.launch(Dispatchers.Default) {
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
                try {
                    imageRepository.searchImages(searchText.value, nextPage)
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is HttpException,
                        is UnknownHostException -> {
                            val signal = ToastSignal(
                                applicationContext.getString(R.string.network_error_message)
                            )

                            signalInternal.send(signal)
                            currentResult
                        }

                        else -> throw throwable
                    }
                }
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