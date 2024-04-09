package com.ho8278.core.pref

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.reflect.Type

class MemoryPreference : Preference {

    private val cache = mutableMapOf<String, Any>()
    private val mutex = Mutex()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> getValue(domainName: String, key: String, type: Type): T? {
        return mutex.withLock {
            cache[getCacheKey(domainName, key)] as? T
        }
    }

    override suspend fun <T : Any> putValue(domainName: String, key: String, value: T, type: Type) {
        mutex.withLock {
            cache[getCacheKey(domainName, key)] = value
        }
    }

    override suspend fun removeValue(domainName: String, key: String) {
        mutex.withLock {
            cache.remove(getCacheKey(domainName, key))
        }
    }

    override suspend fun clear(domainName: String) {
        mutex.withLock {
            cache.clear()
        }
    }

    private fun getCacheKey(domainName: String, key: String): String {
        return "$domainName:$key"
    }
}