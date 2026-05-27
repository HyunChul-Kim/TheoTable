package com.theo.theotable.compose

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
