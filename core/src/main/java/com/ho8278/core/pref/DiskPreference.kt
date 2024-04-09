package com.ho8278.core.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ho8278.core.serialize.Serializer
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.reflect.Type

class DiskPreference(
    private val context: Context,
    private val serializer: Serializer,
) : Preference {

    private val preferenceCache = mutableMapOf<String, SharedPreferences>()
    private val mutex = Mutex()

    private fun getPreference(domainName: String): SharedPreferences {
        return preferenceCache[domainName] ?: kotlin.run {
            val sharedPreference = context.getSharedPreferences(domainName, Context.MODE_PRIVATE)
            preferenceCache[domainName] = sharedPreference
            sharedPreference
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override suspend fun <T : Any> getValue(domainName: String, key: String, type: Type): T? {
        return mutex.withLock {
            val sharedPreference = getPreference(domainName)

            when (type) {
                Int::class.java -> sharedPreference.getInt(key, 0)
                String::class.java -> sharedPreference.getString(key, null)
                Boolean::class.java -> sharedPreference.getBoolean(key, false)
                Float::class.java -> sharedPreference.getFloat(key, 0.0f)
                else -> {
                    val valueString = sharedPreference.getString(key, null)
                    valueString?.let { serializer.deserialize<T>(valueString, type) }
                }
            } as? T
        }
    }

    override suspend fun <T : Any> putValue(domainName: String, key: String, value: T, type: Type) {
        mutex.withLock {
            val sharedPreference = getPreference(domainName)
            when (type) {
                Int::class.java -> sharedPreference.edit { putInt(key, value as Int) }
                String::class.java -> sharedPreference.edit { putString(key, value as String) }
                Boolean::class.java -> sharedPreference.edit { putBoolean(key, value as Boolean) }
                Float::class.java -> sharedPreference.edit { putFloat(key, value as Float) }
                else -> {
                    val serializedString = serializer.serialize<T>(value, type)
                    sharedPreference.edit { putString(key, serializedString) }
                }
            }
        }
    }

    override suspend fun removeValue(domainName: String, key: String) {
        mutex.withLock {
            val sharedPreferences = getPreference(domainName)

            sharedPreferences.edit { remove(key) }
        }
    }

    override suspend fun clear(domainName: String) {
        mutex.withLock {
            val sharedPreference = getPreference(domainName)

            sharedPreference.edit { clear() }
        }
    }
}