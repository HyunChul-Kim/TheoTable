package com.theo.theotable.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

@Immutable
data class TheoTableTextStyles(
    val headerTextStyle: TextStyle,
    val cellTextStyle: TextStyle,
)

internal val LocalTheoTableTextStyles = staticCompositionLocalOf {
    TheoTableTextStyles(
        headerTextStyle = TextStyle.Default,
        cellTextStyle = TextStyle.Default,
    )
}