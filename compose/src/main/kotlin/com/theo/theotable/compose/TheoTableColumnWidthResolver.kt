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
            (cellPadding.calculateLeftPadding(layoutDirection) +
                    cellPadding.calculateRightPadding(layoutDirection)
            ).roundToPx()
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

                    var measuredPx = 0

                    if(width.includeHeader) {
                        measuredPx = max(
                            measuredPx,
                            column.headerWidthHint?.measureWidthPx(textMeasurer, density, defaultHeaderTextStyle) ?: 0,
                        )
                    }

                    val rowHint = column.widthHint
                    if(rowHint != null) {
                        candidateRows.forEach { row ->
                            measuredPx = max(
                                measuredPx,
                                rowHint(row).measureWidthPx(textMeasurer, density, defaultCellTextStyle),
                            )
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
): Int {
    return with(density) {
        when(this@measureWidthPx) {
            is TheoTableWidthHint.Text -> {
                val result = textMeasurer.measure(
                    text = value,
                    style = style ?: defaultTextStyle,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    maxLines = 1,
                )

                result.size.width + leading.roundToPx() + trailing.roundToPx()
            }

            is TheoTableWidthHint.ExactDp -> value.roundToPx()
        }
    }
}
