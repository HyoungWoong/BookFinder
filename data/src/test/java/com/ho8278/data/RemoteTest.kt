package com.ho8278.data

import com.ho8278.core.retrofit.SerializerConverterFactory
import com.ho8278.core.serialize.MoshiSerializer
import com.ho8278.data.remote.NetworkConstant
import com.ho8278.data.remote.service.ImageSearchService
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class RemoteTest {
    lateinit var service: ImageSearchService

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

        service = retrofit.create(ImageSearchService::class.java)
    }

    @Test
    fun `API 가 정상적으로 호출된다`(): Unit = runBlocking {
        val result = service.searchImages("iron")

        println(result)

        assert(result.meta.totalCount != 0)
        assert(result.documents.isNotEmpty())
    }
}