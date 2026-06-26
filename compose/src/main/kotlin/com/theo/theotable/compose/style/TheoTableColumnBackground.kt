package com.theo.theotable.compose.style

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TheoTableColumnBackground(
    val header: Color? = null,
    val cell: Color? = null,
)