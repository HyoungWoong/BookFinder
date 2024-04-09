package com.ho8278.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Favorites(val cards: List<Card>)
