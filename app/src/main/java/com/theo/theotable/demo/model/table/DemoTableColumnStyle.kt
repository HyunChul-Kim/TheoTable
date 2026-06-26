package com.theo.theotable.demo.model.table

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class DemoTableColumnStyle(
    val cellAlignment: Alignment = Alignment.CenterStart,
    val headerBackground: Color? = null,
    val cellBackground: Color? = null,
)