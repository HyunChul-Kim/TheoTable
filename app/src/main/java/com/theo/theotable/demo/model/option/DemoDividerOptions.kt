package com.theo.theotable.demo.model.option

import androidx.compose.ui.graphics.Color
import com.theo.theotable.compose.style.TheoTableDividerColors

data class DemoDividerOptions(
    val border: Boolean = true,
    val headerVertical: Boolean = true,
    val headerHorizontal: Boolean = true,
    val cellVertical: Boolean = true,
    val cellHorizontal: Boolean = true,
    val accentColors: Boolean = true,
)

fun DemoDividerOptions.toDividerColors(): TheoTableDividerColors {
    val defaultColor = Color(0xFFE0E0E0)

    val borderColor = if(accentColors) Color(0xFF6B7280) else defaultColor
    val headerVerticalColor = if(accentColors) Color(0xFF2563EB) else defaultColor
    val headerHorizontalColor = if(accentColors) Color(0xFF9333EA) else defaultColor
    val cellVerticalColor = if(accentColors) Color(0xFF16A34A) else defaultColor
    val cellHorizontalColor = if(accentColors) Color(0xFFEA580C) else defaultColor

    return TheoTableDividerColors(
        border = if(border) borderColor else Color.Transparent,
        headerVertical = if(headerVertical) headerVerticalColor else Color.Transparent,
        headerHorizontal = if(headerHorizontal) headerHorizontalColor else Color.Transparent,
        cellVertical = if(cellVertical) cellVerticalColor else Color.Transparent,
        cellHorizontal = if(cellHorizontal) cellHorizontalColor else Color.Transparent,
    )
}