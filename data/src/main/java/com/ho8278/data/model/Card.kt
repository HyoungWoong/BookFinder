package com.ho8278.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Card(
    val characterId: Int,
    val thumbnail: String,
    val name: String,
    val description: String,
)
