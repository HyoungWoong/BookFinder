package com.ho8278.bookfinder.search

import androidx.compose.runtime.Immutable
import com.ho8278.bookfinder.common.ItemHolder

@Immutable
data class SearchUiState(
    val searchText: String,
    val searchedList: List<ItemHolder>,
    val isLoading: Boolean,
    val isEnd: Boolean,
)
