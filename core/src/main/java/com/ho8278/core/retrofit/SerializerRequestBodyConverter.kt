package com.ho8278.core.retrofit

import com.ho8278.core.serialize.Serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import java.lang.reflect.Type

class SerializerRequestBodyConverter<T>(
    private val serializer: Serializer,
    private val type: Type
) : Converter<T, RequestBody> {
    override fun convert(value: T): RequestBody? {
        val json = serializer.serialize(value, type)

        return RequestBody.create(MediaType.get("application/json; charset=UTF-8"), json)
    }
}