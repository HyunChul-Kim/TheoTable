package com.theo.theotable.compose.column

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
sealed interface TheoTableWidthHint {
    @Immutable
    data class Text(
        val value: String,
        val style: TextStyle? = null,
        val leading: Dp = 0.dp,
        val trailing: Dp = 0.dp,
        val maxLines: Int = 1,
        val overflow: TextOverflow = TextOverflow.Ellipsis,
        val softWrap: Boolean = maxLines > 1,
    ): TheoTableWidthHint {
        init {
            require(maxLines > 0) { "Text width hint maxLines must be greater than zero." }
        }
    }

    @Immutable
    data class ExactDp(
        val value: Dp,
    ): TheoTableWidthHint
}