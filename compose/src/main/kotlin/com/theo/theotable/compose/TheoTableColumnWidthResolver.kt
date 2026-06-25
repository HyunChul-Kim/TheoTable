package com.theo.theotable.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.max

@Composable
internal fun <T> rememberResolvedColumnWidths(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    cellPadding: PaddingValues,
    defaultHeaderTextStyle: TextStyle,
    defaultCellTextStyle: TextStyle,
): List<Dp> {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = rememberTextMeasurer()

    return remember(
        rows,
        columns,
        cellPadding,
        defaultHeaderTextStyle,
        defaultCellTextStyle,
        density.density,
        density.fontScale,
        layoutDirection,
    ) {
        val horizontalPaddingPx = with(density) {
            cellPadding.calculateLeftPadding(layoutDirection).roundToPx() +
                    cellPadding.calculateRightPadding(layoutDirection).roundToPx()
        }

        columns.map { column ->
            when(val width = column.width) {
                is TheoTableColumnWidth.Fixed -> width.value
                is TheoTableColumnWidth.Content -> {
                    require(
                        width.strategy !is TheoTableContentWidthStrategy.ExactAllRows ||
                                column.widthHint != null
                    ) {
                        "ExactAllRows requires widthHint. Provide widthHint or use Sampled/Fixed width."
                    }

                    val candidateRows = when(val strategy = width.strategy) {
                        is TheoTableContentWidthStrategy.Sampled -> rows.take(strategy.count)
                        is TheoTableContentWidthStrategy.ExactAllRows -> rows
                    }

                    val maxMeasuredPx = with(density) {
                        (width.max.roundToPx() - horizontalPaddingPx).coerceAtLeast(0)
                    }

                    var measuredPx = 0

                    if(width.includeHeader) {
                        measuredPx = max(
                            measuredPx,
                            column.headerWidthHint?.measureWidthPx(
                                textMeasurer = textMeasurer,
                                density = density,
                                defaultTextStyle = defaultHeaderTextStyle,
                                maxWidthPx = maxMeasuredPx,
                            ) ?: 0,
                        )
                    }

                    val rowHint = column.widthHint
                    if(rowHint != null) {
                        val measuredHintWidths = mutableMapOf<TheoTableWidthHint, Int>()

                        for(row in candidateRows) {
                            val hint = rowHint(row)
                            val rowMeasurePx = measuredHintWidths.getOrPut(hint) {
                                hint.measureWidthPx(
                                    textMeasurer = textMeasurer,
                                    density = density,
                                    defaultTextStyle = defaultCellTextStyle,
                                    maxWidthPx = maxMeasuredPx
                                )
                            }

                            measuredPx = max(measuredPx, rowMeasurePx)

                            if(measuredPx >= maxMeasuredPx) {
                                break
                            }
                        }
                    }

                    with(density) {
                        (measuredPx + horizontalPaddingPx)
                            .coerceAtLeast(width.min.roundToPx())
                            .coerceAtMost(width.max.roundToPx())
                            .toDp()
                    }
                }
            }
        }
    }
}

private fun TheoTableWidthHint.measureWidthPx(
    textMeasurer: TextMeasurer,
    density: Density,
    defaultTextStyle: TextStyle,
    maxWidthPx: Int,
): Int {
    return when(this) {
        is TheoTableWidthHint.Text -> measureTextWidthPx(
            textMeasurer = textMeasurer,
            density = density,
            defaultTextStyle = defaultTextStyle,
            maxWidthPx = maxWidthPx,
        )

        is TheoTableWidthHint.ExactDp -> with(density) {
            value.roundToPx()
        }
    }
}

private fun TheoTableWidthHint.Text.measureTextWidthPx(
    textMeasurer: TextMeasurer,
    density: Density,
    defaultTextStyle: TextStyle,
    maxWidthPx: Int,
): Int = with(density) {
    val extraPx = leading.roundToPx() + trailing.roundToPx()
    val textMaxWidthPx = (maxWidthPx - extraPx).coerceAtLeast(0)
    val resolvedStyle = style ?: defaultTextStyle

    val textWidthPx = measureMinimumFittingTextWidthPx(
        textMeasurer = textMeasurer,
        text = value,
        style = resolvedStyle,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        maxWidthPx = textMaxWidthPx,
    )

    textWidthPx + extraPx
}

private fun measureMinimumFittingTextWidthPx(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    maxLines: Int,
    overflow: TextOverflow,
    softWrap: Boolean,
    maxWidthPx: Int,
): Int {
    if(text.isEmpty() || maxWidthPx <= 0) return 0

    fun fits(widthPx: Int): Boolean {
        val result = textMeasurer.measure(
            text = text,
            style = style,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = Constraints(maxWidth = widthPx),
        )

        val hasEllipsis = (0 until result.lineCount).any { lineIndex ->
            result.isLineEllipsized(lineIndex)
        }

        return !result.hasVisualOverflow && !hasEllipsis
    }

    if(!fits(maxWidthPx)) return maxWidthPx

    var low = 1
    var high = maxWidthPx
    var answer = maxWidthPx

    while(low <= high) {
        val mid = low + (high - low) / 2

        if(fits(mid)) {
            answer = mid
            high = mid - 1
        } else {
            low = mid + 1
        }
    }

    return answer
}
