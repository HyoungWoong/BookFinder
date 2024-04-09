package com.ho8278.core.serialize

import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class MoshiSerializer(private val moshi: Moshi) : Serializer {
    override fun <T> serialize(data: T?, type: Type): String? {
        return data?.let {
            val adapter = moshi.adapter<T>(type)
            adapter.toJson(it)
        }
    }

    override fun <T> deserialize(serializedString: String, type: Type): T? {
        val adapter = moshi.adapter<T>(type)

        return adapter.fromJson(serializedString)
    }
}