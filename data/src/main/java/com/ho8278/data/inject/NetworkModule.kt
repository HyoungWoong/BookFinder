package com.ho8278.data.inject

import com.ho8278.core.retrofit.SerializerConverterFactory
import com.ho8278.core.serialize.Serializer
import com.ho8278.data.remote.NetworkConstant
import com.ho8278.data.remote.service.MarbleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(serializer: Serializer): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstant.ENDPOINT)
            .addConverterFactory(SerializerConverterFactory(serializer))
            .build()
    }

    @Provides
    @Singleton
    fun provideMarbleService(retrofit: Retrofit): MarbleService {
        return retrofit.create(MarbleService::class.java)
    }
}