package com.theo.theotable.compose.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceIn
import com.theo.theotable.compose.column.TheoTableColumn
import com.theo.theotable.compose.column.TheoTableColumnWidth
import com.theo.theotable.compose.column.TheoTableColumnWidthResolvingMode
import com.theo.theotable.compose.column.TheoTableContentWidthStrategy
import com.theo.theotable.compose.column.TheoTableWidthHint
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

internal data class TheoTableResolvedColumnWidths(
    val widths: List<Dp>,
    val isResolving: Boolean,
)

@Composable
internal fun <T> rememberResolvedColumnWidths(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    cellPadding: PaddingValues,
    defaultHeaderTextStyle: TextStyle,
    defaultCellTextStyle: TextStyle,
    resolvingMode: TheoTableColumnWidthResolvingMode,
): TheoTableResolvedColumnWidths {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = rememberTextMeasurer()

    return when(resolvingMode) {
        is TheoTableColumnWidthResolvingMode.Immediate -> {
            val widths = remember(
                rows,
                columns,
                cellPadding,
                defaultHeaderTextStyle,
                defaultCellTextStyle,
                density.density,
                density.fontScale,
                layoutDirection,
            ) {
                resolveColumnWidthsImmediately(
                    rows = rows,
                    columns = columns,
                    cellPadding = cellPadding,
                    defaultHeaderTextStyle = defaultHeaderTextStyle,
                    defaultCellTextStyle = defaultCellTextStyle,
                    density = density,
                    layoutDirection = layoutDirection,
                    textMeasurer = textMeasurer,
                )
            }

            TheoTableResolvedColumnWidths(
                widths = widths,
                isResolving = false,
            )
        }

        is TheoTableColumnWidthResolvingMode.Deferred -> {
            val fallbackWidths = remember(
                columns,
                resolvingMode.fallbackWidth,
            ) {
                resolveFallbackColumnWidths(
                    columns = columns,
                    fallbackWidth = resolvingMode.fallbackWidth,
                )
            }

            var resolved by remember(columns) {
                mutableStateOf(
                    TheoTableResolvedColumnWidths(
                        widths = fallbackWidths,
                        isResolving = true,
                    )
                )
            }

            LaunchedEffect(
                rows,
                columns,
                cellPadding,
                defaultHeaderTextStyle,
                defaultCellTextStyle,
                density.density,
                density.fontScale,
                layoutDirection,
                resolvingMode,
            ) {
                val visibleWidths =
                    if(
                        resolvingMode.keepPreviousWidths &&
                        resolved.widths.size == columns.size
                    ) {
                        resolved.widths
                    } else {
                        fallbackWidths
                    }

                resolved = TheoTableResolvedColumnWidths(
                    widths = visibleWidths,
                    isResolving = true,
                )

                withFrameNanos { }

                val startedAt = TimeSource.Monotonic.markNow()

                val frameYieldController = WidthResolveFrameYieldController(
                    frameBudgetMillis = WidthResolveFrameBudgetMillis,
                )

                val widths = resolveColumnWidthsDeferred(
                    rows = rows,
                    columns = columns,
                    cellPadding = cellPadding,
                    defaultHeaderTextStyle = defaultHeaderTextStyle,
                    defaultCellTextStyle = defaultCellTextStyle,
                    density = density,
                    layoutDirection = layoutDirection,
                    textMeasurer = textMeasurer,
                    rowsPerChunk = resolvingMode.rowsPerChunk,
                    frameYieldController = frameYieldController,
                )

                val remainingMillis =
                    resolvingMode.minimumLoadingDurationMillis -
                            startedAt.elapsedNow().inWholeMilliseconds

                if(remainingMillis > 0L) {
                    delay(remainingMillis.milliseconds)
                }

                resolved = TheoTableResolvedColumnWidths(
                    widths = widths,
                    isResolving = false,
                )
            }

            resolved
        }
    }
}

private fun <T> resolveFallbackColumnWidths(
    columns: List<TheoTableColumn<T>>,
    fallbackWidth: Dp,
): List<Dp> {
    return columns.map { column ->
        when(val width = column.width) {
            is TheoTableColumnWidth.Fixed -> width.value
            is TheoTableColumnWidth.Content -> fallbackWidth.coerceIn(width.min, width.max)
        }
    }
}

private fun <T> resolveColumnWidthsImmediately(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    cellPadding: PaddingValues,
    defaultHeaderTextStyle: TextStyle,
    defaultCellTextStyle: TextStyle,
    density: Density,
    layoutDirection: LayoutDirection,
    textMeasurer: TextMeasurer,
): List<Dp> {
    val horizontalPaddingPx = with(density) {
        cellPadding.calculateLeftPadding(layoutDirection).roundToPx() +
                cellPadding.calculateRightPadding(layoutDirection).roundToPx()
    }

    return columns.map { column ->
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
                                maxWidthPx = maxMeasuredPx,
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

private suspend fun <T> resolveColumnWidthsDeferred(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    cellPadding: PaddingValues,
    defaultHeaderTextStyle: TextStyle,
    defaultCellTextStyle: TextStyle,
    density: Density,
    layoutDirection: LayoutDirection,
    textMeasurer: TextMeasurer,
    rowsPerChunk: Int,
    frameYieldController: WidthResolveFrameYieldController,
): List<Dp> {
    val horizontalPaddingPx = with(density) {
        cellPadding.calculateLeftPadding(layoutDirection).roundToPx() +
                cellPadding.calculateRightPadding(layoutDirection).roundToPx()
    }

    return columns.map { column ->
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
                    var measuredRows = 0

                    for(row in candidateRows) {
                        val hint = rowHint(row)
                        val rowMeasurePx = measuredHintWidths.getOrPut(hint) {
                            hint.measureWidthPx(
                                textMeasurer = textMeasurer,
                                density = density,
                                defaultTextStyle = defaultCellTextStyle,
                                maxWidthPx = maxMeasuredPx,
                            )
                        }

                        measuredPx = max(measuredPx, rowMeasurePx)

                        measuredRows++

                        if(measuredRows % rowsPerChunk == 0) {
                            frameYieldController.yieldIfNeeded()
                        }

                        if(measuredPx >= maxMeasuredPx) {
                            break
                        }
                    }
                }

                val resolvedWidth = with(density) {
                    (measuredPx + horizontalPaddingPx)
                        .coerceAtLeast(width.min.roundToPx())
                        .coerceAtMost(width.max.roundToPx())
                        .toDp()
                }

                resolvedWidth
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

    val textWidthPx = measurePreferredTextWidthPx(
        textMeasurer = textMeasurer,
        text = value,
        style = resolvedStyle,
        maxWidthPx = textMaxWidthPx,
    )

    textWidthPx + extraPx
}

private fun measurePreferredTextWidthPx(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    maxWidthPx: Int,
): Int {
    if(text.isEmpty() || maxWidthPx <= 0) return 0

    val naturalSingleLineWidthPx = textMeasurer.measure(
        text = text,
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = false,
        maxLines = 1,
        constraints = Constraints(),
    ).size.width

    return naturalSingleLineWidthPx.coerceAtMost(maxWidthPx)
}
