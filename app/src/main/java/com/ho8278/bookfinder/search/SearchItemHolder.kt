package com.ho8278.bookfinder.search

import androidx.compose.runtime.Immutable
import com.ho8278.data.model.Image

@Immutable
data class SearchItemHolder(
    val image: Image,
    val isFavorite: Boolean
)
