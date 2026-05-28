package com.theo.theotable.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

object TheoTableDefaults {
    val HeaderBackground = Color(0xFFF7F7F7)
    val CellBackground = Color.Transparent
    val SelectedRowBackground = Color(0xFFE8F1FF)
    val BorderColor = Color(0xFFE0E0E0)

    val HeaderHeight = 44.dp
    val RowMinHeight = 44.dp
    val CellHorizontalPadding = 12.dp
    val CellVerticalPadding = 8.dp

    fun style(
        text: TheoTableTextStyle = textStyle(),
        background: TheoTableBackgroundStyle = backgroundStyle(),
        divider: TheoTableDividerColors = dividerColors(),
    ): TheoTableStyle {
        return TheoTableStyle(
            text = text,
            background = background,
            divider = divider,
        )
    }

    fun textStyle(
        base: TextStyle = TextStyle.Default,
        header: TextStyle = base,
        cell: TextStyle = base,
    ): TheoTableTextStyle {
        return TheoTableTextStyle(
            base = base,
            header = header,
            cell = cell,
        )
    }

    fun backgroundStyle(
        header: Color = HeaderBackground,
        cell: Color = CellBackground,
        selectedRow: Color = SelectedRowBackground,
    ): TheoTableBackgroundStyle {
        return TheoTableBackgroundStyle(
            header = header,
            cell = cell,
            selectedRow = selectedRow,
        )
    }

    fun dividerColors(
        border: Color = BorderColor,
        vertical: Color = BorderColor,
        horizontal: Color = BorderColor,
        headerVertical: Color = vertical,
        headerHorizontal: Color = horizontal,
        cellVertical: Color = vertical,
        cellHorizontal: Color = horizontal,
    ): TheoTableDividerColors {
        return TheoTableDividerColors(
            border = border,
            headerVertical = headerVertical,
            headerHorizontal = headerHorizontal,
            cellVertical = cellVertical,
            cellHorizontal = cellHorizontal,
        )
    }
}
