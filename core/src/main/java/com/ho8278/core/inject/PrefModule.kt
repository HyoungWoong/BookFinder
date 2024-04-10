package com.ho8278.core.inject

import android.content.Context
import com.ho8278.core.pref.CachePreference
import com.ho8278.core.pref.DiskPreference
import com.ho8278.core.pref.Preference
import com.ho8278.core.serialize.Serializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PrefModule {

    @Provides
    @Singleton
    fun providePreference(
        @ApplicationContext context: Context,
        serializer: Serializer
    ): Preference {
        return CachePreference(DiskPreference(context, serializer))
    }
}