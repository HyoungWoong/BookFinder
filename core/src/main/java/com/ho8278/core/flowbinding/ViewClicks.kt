package com.ho8278.core.flowbinding

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> {
    return callbackFlow {
        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = Unit

            override fun onViewDetachedFromWindow(v: View) {
                close()
            }
        }

        addOnAttachStateChangeListener(attachListener)

        setOnClickListener {
            trySend(Unit)
        }

        awaitClose {
            removeOnAttachStateChangeListener(attachListener)
        }
    }
}