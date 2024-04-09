package com.ho8278.core.pref

import java.lang.reflect.Type

interface Preference {
    suspend fun <T : Any> getValue(domainName: String, key: String, type: Type): T?
    suspend fun <T : Any> putValue(domainName: String, key: String, value: T, type: Type)
    suspend fun removeValue(domainName: String, key: String)
    suspend fun clear(domainName: String)
}