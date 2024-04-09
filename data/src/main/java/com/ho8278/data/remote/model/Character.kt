package com.ho8278.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Character(
    val id: Int?,
    val name: String?,
    val description: String?,
    val thumbnail: Image?,
)