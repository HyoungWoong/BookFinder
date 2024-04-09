package com.ho8278.data.model

data class SearchResult(
    val total: Int,
    val offset: Int,
    val results: List<Card>
)
