package com.ho8278.bookfinder.common

import androidx.compose.runtime.Immutable
import com.ho8278.data.model.Image

@Immutable
data class ItemHolder(
    val image: Image,
    val isFavorite: Boolean
)
