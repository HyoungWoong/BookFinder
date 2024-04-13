package com.ho8278.bookfinder.search

import androidx.compose.runtime.Immutable

@Immutable
data class SearchUiState(
    val searchText: String,
    val searchedList: List<SearchItemHolder>,
    val isLoading: Boolean,
    val isEnd: Boolean,
)
