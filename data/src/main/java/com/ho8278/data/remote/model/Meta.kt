package com.ho8278.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "total_count")
    val totalCount: Int,
    @Json(name = "pageable_count")
    val pageableCount: Int,
    @Json(name = "is_end")
    val isEnd: Boolean,
)
