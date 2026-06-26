package com.theo.theotable.compose.internal

import androidx.compose.runtime.withFrameNanos
import kotlin.time.TimeSource

internal const val WidthResolveFrameBudgetMillis = 8L

internal class WidthResolveFrameYieldController(
    private val frameBudgetMillis: Long,
) {
    private var frameStartedAt = TimeSource.Monotonic.markNow()

    suspend fun yieldIfNeeded() {
        if(frameBudgetMillis <= 0L) return

        val elapsedMillis = frameStartedAt.elapsedNow().inWholeMilliseconds
        if(elapsedMillis < frameBudgetMillis) return

        withFrameNanos { }

        frameStartedAt = TimeSource.Monotonic.markNow()
    }
}
