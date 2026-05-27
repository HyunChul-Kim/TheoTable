package com.theo.theotable.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
sealed interface TheoTableColumnWidth {

    @Immutable
    data class Fixed(
        val value: Dp,
    ): TheoTableColumnWidth {
        init {
            require(value > 0.dp) { "Fixed column width must be greater than zero." }
        }
    }

    @Immutable
    data class Content(
        val strategy: TheoTableContentWidthStrategy = TheoTableContentWidthStrategy.Sampled(),
        val min: Dp = 64.dp,
        val max: Dp = 320.dp,
        val includeHeader: Boolean = true,
    ): TheoTableColumnWidth {
        init {
            require(min >= 0.dp) { "Content min width must be greater than or equal to zero." }
            require(max >= min) { "Content max width must be greater than or equal to min width." }
        }
    }
}

@Immutable
sealed interface TheoTableContentWidthStrategy {
    @Immutable
    data class Sampled(
        val count: Int = 100,
    ): TheoTableContentWidthStrategy {
        init {
            require(count > 0) { "Sampled count must be greater than zero." }
        }
    }

    data object ExactAllRows: TheoTableContentWidthStrategy
}