package com.ho8278.data

import com.ho8278.core.pref.MemoryPreference
import com.ho8278.core.retrofit.SerializerConverterFactory
import com.ho8278.core.serialize.MoshiSerializer
import com.ho8278.data.local.FavoritePref
import com.ho8278.data.model.Image
import com.ho8278.data.remote.NetworkConstant
import com.ho8278.data.remote.service.ImageSearchService
import com.ho8278.data.repository.ImageRepository
import com.ho8278.data.repository.ImageRepositoryImpl
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class RepositoryTest {

    lateinit var repository: ImageRepository

    @Before
    fun setup() {
        val moshi = Moshi.Builder()
            .build()
        val serializer = MoshiSerializer(moshi)
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor {
                val request = it.request()
                    .newBuilder()
                    .header(
                        NetworkConstant.AUTHORIZATION_HEADER,
                        NetworkConstant.AUTHORIZATION_PREFIX + BuildConfig.API_KEY
                    )
                    .build()

                it.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConstant.ENDPOINT)
            .addConverterFactory(SerializerConverterFactory(serializer))
            .client(okhttp)
            .build()

        val service = retrofit.create(ImageSearchService::class.java)

        val favoritePref = FavoritePref(MemoryPreference())

        repository = ImageRepositoryImpl(service, favoritePref)
    }

    @Test
    fun `검색 쿼리로 마블 캐릭터를 검색할 수 있다`(): Unit = runBlocking {
        val searchResult = repository.searchImages("iron", 1)

        assert(searchResult.total != 0)
        assert(searchResult.results.isNotEmpty())
    }

    @Test
    fun `현재 저장된 favorite 을 저장하고 가져올 수 있다`(): Unit = runBlocking {
        repository.setFavorite(Image("https://test.test.com"))
        repository.setFavorite(Image("https://test1.test.com"))
        repository.setFavorite(Image("https://test2.test.com"))
        repository.setFavorite(Image("https://test3.test.com"))
        repository.setFavorite(Image("https://test4.test.com"))

        val favoriteIds = repository.getFavorites()

        assert(favoriteIds[0] == Image("https://test.test.com"))
        assert(favoriteIds[1] == Image("https://test1.test.com"))
        assert(favoriteIds[2] == Image("https://test2.test.com"))
        assert(favoriteIds[3] == Image("https://test3.test.com"))
        assert(favoriteIds[4] == Image("https://test4.test.com"))
    }

    @Test
    fun `현재 저장된 favorite 을 삭제할 수 있다`(): Unit = runBlocking {
        repository.setFavorite(Image("https://test.test.com"))
        repository.setFavorite(Image("https://test1.test.com"))
        repository.setFavorite(Image("https://test2.test.com"))
        repository.setFavorite(Image("https://test3.test.com"))
        repository.setFavorite(Image("https://test4.test.com"))

        repository.removeFavorite(Image("https://test2.test.com"))

        val favoriteIds = repository.getFavorites()

        assert(favoriteIds[0] == Image("https://test.test.com"))
        assert(favoriteIds[1] == Image("https://test1.test.com"))
        assert(favoriteIds[2] == Image("https://test3.test.com"))
        assert(favoriteIds[3] == Image("https://test4.test.com"))
    }
}