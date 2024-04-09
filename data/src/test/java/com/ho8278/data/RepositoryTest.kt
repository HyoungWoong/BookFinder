package com.ho8278.data

import com.ho8278.core.pref.MemoryPreference
import com.ho8278.core.retrofit.SerializerConverterFactory
import com.ho8278.core.serialize.MoshiSerializer
import com.ho8278.data.local.FavoritePref
import com.ho8278.data.model.Card
import com.ho8278.data.remote.NetworkConstant
import com.ho8278.data.remote.service.MarbleService
import com.ho8278.data.repository.MarbleRepository
import com.ho8278.data.repository.MarbleRepositoryImpl
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class RepositoryTest {

    lateinit var repository: MarbleRepository

    @Before
    fun setup() {
        val moshi = Moshi.Builder()
            .build()
        val serializer = MoshiSerializer(moshi)
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConstant.ENDPOINT)
            .addConverterFactory(SerializerConverterFactory(serializer))
            .client(okhttp)
            .build()

        val service = retrofit.create(MarbleService::class.java)

        val favoritePref = FavoritePref(MemoryPreference())

        repository = MarbleRepositoryImpl(service, favoritePref)
    }

    @Test
    fun `검색 쿼리로 마블 캐릭터를 검색할 수 있다`(): Unit = runBlocking {
        val searchResult = repository.search("iron", 0)

        assert(searchResult.total != 0)
        assert(searchResult.results.size == 10)
    }

    @Test
    fun `현재 저장된 favorite 을 저장하고 가져올 수 있다`(): Unit = runBlocking {
        repository.setFavorite(Card(1,"","",""))
        repository.setFavorite(Card(3,"","",""))
        repository.setFavorite(Card(5,"","",""))
        repository.setFavorite(Card(6,"","",""))
        repository.setFavorite(Card(6,"","",""))

        val favoriteIds = repository.getFavorites()

        assert(favoriteIds[0] == Card(1,"","",""))
        assert(favoriteIds[1] == Card(3,"","",""))
        assert(favoriteIds[2] == Card(5,"","",""))
        assert(favoriteIds[3] == Card(6,"","",""))
    }

    @Test
    fun `현재 저장된 favorite 을 삭제할 수 있다`(): Unit = runBlocking {
        repository.setFavorite(Card(1,"","",""))
        repository.setFavorite(Card(3,"","",""))
        repository.setFavorite(Card(5,"","",""))
        repository.setFavorite(Card(6,"","",""))
        repository.setFavorite(Card(6,"","",""))

        repository.removeFavorite(1)

        val favoriteIds = repository.getFavorites()

        assert(favoriteIds[0] == Card(3,"","",""))
        assert(favoriteIds[1] == Card(5,"","",""))
        assert(favoriteIds[2] == Card(6,"","",""))
    }
}