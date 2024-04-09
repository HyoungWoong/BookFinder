package com.ho8278.core.retrofit

import com.ho8278.core.serialize.Serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

class SerializerResponseBodyConverter<T>(
    private val serializer: Serializer,
    private val type: Type
) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T? {
        return serializer.deserialize(value.string(), type)
    }
}