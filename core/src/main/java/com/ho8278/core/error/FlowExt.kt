package com.ho8278.core.error

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.stable(): Flow<T> {
    return flow {
        while (true) {
            try {
                collect { emit(it) }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (ignore: Throwable) {
                delay(1000L)
                ignore.printStackTrace()
            }
        }
    }
}