package com.theo.theotable.demo.model.option

import androidx.compose.ui.unit.dp
import com.theo.theotable.compose.column.TheoTableColumnWidth
import com.theo.theotable.compose.column.TheoTableContentWidthStrategy

data class DemoColumnStructureOption(
    val widthMode: DemoWidthMode = DemoWidthMode.Content,
    val fixedWidthDp: Float = 140f,
    val maxLines: Int = 1,
    val sortable: Boolean = true,
)

fun DemoColumnOptions.toStructureOptions(): DemoColumnStructureOption {
    return DemoColumnStructureOption(
        widthMode = widthMode,
        fixedWidthDp = fixedWidthDp,
        maxLines = maxLines,
        sortable = sortable,
    )
}

fun DemoColumnStructureOption.toWidth(): TheoTableColumnWidth {
    return when(widthMode) {
        DemoWidthMode.Content -> TheoTableColumnWidth.Content(
            strategy = TheoTableContentWidthStrategy.ExactAllRows,
            min = 64.dp,
            max = 520.dp,
        )

        DemoWidthMode.Fixed -> TheoTableColumnWidth.Fixed(fixedWidthDp.dp)
    }
}
