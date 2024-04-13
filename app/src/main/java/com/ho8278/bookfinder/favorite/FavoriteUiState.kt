package com.ho8278.bookfinder.favorite

data class FavoriteUiState(
    val list: List<FavoriteItemHolder>,
    val isInEditMode: Boolean,
)