package com.ho8278.bookfinder.search

import androidx.compose.runtime.Immutable

@Immutable
sealed class SearchUiState(val searchText: String) {

    class Undefined(searchText: String) : SearchUiState(searchText)
    class Success(
        val searchedList: List<SearchItemHolder>,
        val isEnd: Boolean,
        searchText: String,
    ) : SearchUiState(searchText)

    class Loading(searchText: String) : SearchUiState(searchText)

    class Empty(searchText: String) : SearchUiState(searchText)
}