package com.theo.theotable.compose.column

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
sealed interface TheoTableColumnWidthResolvingMode {
    data object Immediate: TheoTableColumnWidthResolvingMode

    data class Deferred(
        val fallbackWidth: Dp = 120.dp,
        val rowsPerChunk: Int = 25,
        val minimumLoadingDurationMillis: Long = 120L,
        val keepPreviousWidths: Boolean = true,
        val renderContentWhileResolving: Boolean = true,
    ): TheoTableColumnWidthResolvingMode {
        init {
            require(fallbackWidth > 0.dp) {
                "Deferred fallback width must be greater than zero."
            }
            require(rowsPerChunk > 0) {
                "Deferred rowsPerChunk must be greater than zero."
            }
            require(minimumLoadingDurationMillis >= 0L) {
                "Deferred minimum loading duration must be greater than or equal to zero."
            }
        }
    }
}