package com.ho8278.bookfinder.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@Composable
fun <T> SingleEvent(
    eventSource: Flow<T>,
    lifecycle: Lifecycle,
    collector: FlowCollector<T>,
) {
    LaunchedEffect(key1 = eventSource) {
        eventSource.flowWithLifecycle(lifecycle)
            .collect(collector)
    }
}