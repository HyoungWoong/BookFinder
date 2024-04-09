package com.ho8278.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchImagesResult(
    val meta: Meta,
    val documents: List<Document>
)
