package com.ho8278.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterDataContainer(
    val offset: Int = 0,
    val limit: Int = 0,
    val total: Int = 0,
    val count: Int = 0,
    val results: List<Character>?,
)