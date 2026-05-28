package com.theo.theotable.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Immutable
data class TheoTableStyle(
    val text: TheoTableTextStyle = TheoTableTextStyle(),
    val background: TheoTableBackgroundStyle = TheoTableBackgroundStyle(),
    val divider: TheoTableDividerColors = TheoTableDividerColors(),
)

@Immutable
data class TheoTableTextStyle(
    val base: TextStyle = TextStyle.Default,
    val header: TextStyle = base,
    val cell: TextStyle = base,
)

@Immutable
data class TheoTableBackgroundStyle(
    val header: Color = TheoTableDefaults.HeaderBackground,
    val cell: Color = TheoTableDefaults.CellBackground,
    val selectedRow: Color = TheoTableDefaults.SelectedRowBackground,
)

@Immutable
data class TheoTableDividerColors(
    val border: Color = TheoTableDefaults.BorderColor,
    val headerVertical: Color = TheoTableDefaults.BorderColor,
    val headerHorizontal: Color = TheoTableDefaults.BorderColor,
    val cellVertical: Color = TheoTableDefaults.BorderColor,
    val cellHorizontal: Color = TheoTableDefaults.BorderColor,
)
