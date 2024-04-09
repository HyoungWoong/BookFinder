package com.ho8278.core.pref

import android.util.LruCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.reflect.Type

class CachePreference(
    private val delegate: Preference
) : Preference {

    private val valueCache = LruCache<String, Any>(CACHE_SIZE)

    private val mutex = Mutex()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> getValue(domainName: String, key: String, type: Type): T? {
        return mutex.withLock {
            valueCache[getCacheKey(domainName, key)] as? T
                ?: run {
                    val delegateValue = delegate.getValue<T>(domainName, key, type)
                    valueCache.put(getCacheKey(domainName, key), delegateValue)
                    delegateValue
                }
        }
    }

    override suspend fun <T : Any> putValue(domainName: String, key: String, value: T, type: Type) {
        mutex.withLock {
            valueCache.put(getCacheKey(domainName, key), value)
            delegate.putValue(domainName, key, value, type)
        }
    }

    override suspend fun removeValue(domainName: String, key: String) {
        mutex.withLock {
            valueCache.remove(getCacheKey(domainName, key))
            delegate.removeValue(domainName, key)
        }
    }

    override suspend fun clear(domainName: String) {
        mutex.withLock {
            valueCache.evictAll()
            delegate.clear(domainName)
        }
    }

    private fun getCacheKey(domainName: String, key: String): String {
        return "$domainName:$key"
    }

    companion object {
        private const val CACHE_SIZE = 256
    }
}