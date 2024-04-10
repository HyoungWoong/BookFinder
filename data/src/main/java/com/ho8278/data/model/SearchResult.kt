package com.ho8278.data.model

data class SearchResult(
    val total: Int,
    val isEnd: Boolean,
    val page: Int,
    val results: List<Image>
)
