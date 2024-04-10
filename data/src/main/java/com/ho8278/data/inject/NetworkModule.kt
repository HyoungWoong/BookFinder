package com.ho8278.data.inject

import com.ho8278.core.retrofit.SerializerConverterFactory
import com.ho8278.core.serialize.Serializer
import com.ho8278.data.BuildConfig
import com.ho8278.data.remote.NetworkConstant
import com.ho8278.data.remote.service.ImageSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(serializer: Serializer, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstant.ENDPOINT)
            .client(okHttpClient)
            .addConverterFactory(SerializerConverterFactory(serializer))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
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
    }

    @Provides
    @Singleton
    fun provideMarbleService(retrofit: Retrofit): ImageSearchService {
        return retrofit.create(ImageSearchService::class.java)
    }
}