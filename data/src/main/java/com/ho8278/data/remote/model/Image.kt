package com.ho8278.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    val path: String?,
    val extension: String?,
)

fun Image.toUrl() = "${path.orEmpty()}.${extension.orEmpty()}"