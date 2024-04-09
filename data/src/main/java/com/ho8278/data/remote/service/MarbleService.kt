package com.ho8278.data.remote.service

import com.ho8278.data.BuildConfig
import com.ho8278.data.remote.model.GetCharactersResult
import retrofit2.http.GET
import retrofit2.http.Query

interface MarbleService {
    @GET("v1/public/characters")
    suspend fun getCharacters(
        @Query("nameStartsWith") nameStartsWith: String,
        @Query("hash") hash: String,
        @Query("ts") ts: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
    ): GetCharactersResult
}