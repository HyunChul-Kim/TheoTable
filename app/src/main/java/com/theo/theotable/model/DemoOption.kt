package com.theo.theotable.model

import androidx.compose.ui.unit.dp
import com.theo.theotable.compose.TheoTableColumnWidth
import com.theo.theotable.compose.TheoTableContentWidthStrategy

internal enum class DemoWidthMode { Content, Fixed }

internal data class DemoColumnOptions(
    val widthMode: DemoWidthMode = DemoWidthMode.Content,
    val fixedWidthDp: Float = 140f,
    val sortable: Boolean = true,
    val headerTint: Boolean = false,
    val cellTint: Boolean = false,
)

internal fun DemoColumnOptions.toWidth(): TheoTableColumnWidth {
    return when(widthMode) {
        DemoWidthMode.Content -> TheoTableColumnWidth.Content(
            strategy = TheoTableContentWidthStrategy.ExactAllRows,
            min = 64.dp,
            max = 320.dp,
        )

        DemoWidthMode.Fixed -> TheoTableColumnWidth.Fixed(fixedWidthDp.dp)
    }
}