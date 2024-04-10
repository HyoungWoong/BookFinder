package com.ho8278.data.inject

import android.content.Context
import com.ho8278.core.pref.CachePreference
import com.ho8278.core.pref.DiskPreference
import com.ho8278.core.pref.Preference
import com.ho8278.core.serialize.MoshiSerializer
import com.ho8278.core.serialize.Serializer
import com.ho8278.data.local.FavoritePref
import com.ho8278.data.remote.service.ImageSearchService
import com.ho8278.data.repository.ImageRepository
import com.ho8278.data.repository.ImageRepositoryImpl
import com.ho8278.data.repository.TabRepository
import com.ho8278.data.repository.TabRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSerializer(): Serializer {
        return MoshiSerializer(Moshi.Builder().build())
    }

    @Provides
    @Singleton
    fun providePreference(
        @ApplicationContext context: Context,
        serializer: Serializer
    ): Preference {
        return CachePreference(DiskPreference(context, serializer))
    }

    @Provides
    @Singleton
    fun provideMarbleRepository(
        imageSearchService: ImageSearchService,
        favoritePref: FavoritePref
    ): ImageRepository {
        return ImageRepositoryImpl(imageSearchService, favoritePref)
    }

    @Provides
    @Singleton
    fun provideTabRepository(
        favoritePref: FavoritePref
    ): TabRepository {
        return TabRepositoryImpl(favoritePref)
    }
}