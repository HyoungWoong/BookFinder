package com.ho8278.bookfinder.search

import com.ho8278.bookfinder.common.ItemHolder

data class SearchUiState(
    val searchedList: List<ItemHolder>,
    val isLoading: Boolean,
    val isEnd: Boolean,
)
