package com.ho8278.core.flowbinding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun EditText.textChanges(): Flow<String> {
    return callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) return

                trySend(s.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        addTextChangedListener(textWatcher)

        awaitClose { removeTextChangedListener(textWatcher) }
    }
        .onStart { emit(text.toString()) }
}