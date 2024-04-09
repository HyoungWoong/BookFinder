package com.ho8278.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    val thumbnailUrl:String
)
