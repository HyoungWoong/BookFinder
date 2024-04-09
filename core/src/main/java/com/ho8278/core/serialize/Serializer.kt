package com.ho8278.core.serialize

import java.lang.reflect.Type

interface Serializer {
    fun <T> serialize(data: T?, type: Type): String?

    fun <T> deserialize(serializedString: String, type: Type): T?
}