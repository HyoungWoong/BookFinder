package com.ho8278.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCharactersResult(
    val code: Int = 0,
    val data: CharacterDataContainer?,
)
