package com.theo.theotable.compose

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class TheoTableColumnLayout(
    val frozen: TheoTableColumnRange,
    val scrollable: TheoTableColumnRange,
) {
    val hasFrozenColumns: Boolean = !frozen.isEmpty
    val hasScrollableColumns: Boolean = !scrollable.isEmpty
}

internal data class TheoTableColumnRange(
    val startIndex: Int,
    val endIndex: Int,
) {
    init {
        require(startIndex >= 0) { "startIndex must be non-negative." }
        require(endIndex >= startIndex) { "endIndex must be greater than or equal to startIndex." }
    }

    val isEmpty: Boolean = startIndex == endIndex
}

internal fun resolveTheoTableColumnLayout(
    columnCount: Int,
    frozenColumnCount: Int,
): TheoTableColumnLayout {
    require(columnCount >= 0) { "columnCount must be non-negative." }

    val resolvedFrozenColumnCount = frozenColumnCount.coerceIn(0, columnCount)

    return TheoTableColumnLayout(
        frozen = TheoTableColumnRange(
            startIndex = 0,
            endIndex = resolvedFrozenColumnCount,
        ),
        scrollable = TheoTableColumnRange(
            startIndex = resolvedFrozenColumnCount,
            endIndex = columnCount,
        ),
    )
}

internal data class TheoTableContentPaddingLayout(
    val frozenStart: Dp,
    val frozenEnd: Dp,
    val scrollableStart: Dp,
    val scrollableEnd: Dp,
)

internal fun resolveTheoTableContentPaddingLayout(
    columnLayout: TheoTableColumnLayout,
    startPadding: Dp,
    endPadding: Dp,
): TheoTableContentPaddingLayout {
    require(startPadding >= 0.dp) { "startPadding must be non-negative." }
    require(endPadding >= 0.dp) { "endPadding must be non-negative." }

    return when {
        columnLayout.hasFrozenColumns && columnLayout.hasScrollableColumns -> {
            TheoTableContentPaddingLayout(
                frozenStart = startPadding,
                frozenEnd = 0.dp,
                scrollableStart = 0.dp,
                scrollableEnd = endPadding,
            )
        }

        columnLayout.hasFrozenColumns -> {
            TheoTableContentPaddingLayout(
                frozenStart = startPadding,
                frozenEnd = endPadding,
                scrollableStart = 0.dp,
                scrollableEnd = 0.dp,
            )
        }

        columnLayout.hasScrollableColumns -> {
            TheoTableContentPaddingLayout(
                frozenStart = 0.dp,
                frozenEnd = 0.dp,
                scrollableStart = startPadding,
                scrollableEnd = endPadding,
            )
        }

        else -> {
            TheoTableContentPaddingLayout(
                frozenStart = 0.dp,
                frozenEnd = 0.dp,
                scrollableStart = 0.dp,
                scrollableEnd = 0.dp,
            )
        }
    }
}
