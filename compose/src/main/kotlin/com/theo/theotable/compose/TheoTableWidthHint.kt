package com.theo.theotable.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
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
    ): TheoTableWidthHint

    @Immutable
    data class ExactDp(
        val value: Dp,
    ): TheoTableWidthHint
}