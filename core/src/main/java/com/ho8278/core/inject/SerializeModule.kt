package com.ho8278.core.inject

import com.ho8278.core.serialize.MoshiSerializer
import com.ho8278.core.serialize.Serializer
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializeModule {

    @Provides
    @Singleton
    fun provideSerializer(): Serializer {
        return MoshiSerializer(Moshi.Builder().build())
    }
}