package com.theo.theotable.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theo.theotable.compose.column.TheoTableColumn
import com.theo.theotable.compose.column.TheoTableColumnWidthResolvingMode
import com.theo.theotable.compose.internal.rememberResolvedColumnWidths
import com.theo.theotable.compose.layout.TheoTableColumnLayout
import com.theo.theotable.compose.layout.TheoTableColumnRange
import com.theo.theotable.compose.layout.TheoTableContentPaddingLayout
import com.theo.theotable.compose.layout.TheoTableRowHeight
import com.theo.theotable.compose.layout.resolveTheoTableColumnLayout
import com.theo.theotable.compose.layout.resolveTheoTableContentPaddingLayout
import com.theo.theotable.compose.state.TheoTableHeaderState
import com.theo.theotable.compose.state.TheoTableState
import com.theo.theotable.compose.state.rememberTheoTableState
import com.theo.theotable.compose.style.LocalTheoTableTextStyles
import com.theo.theotable.compose.style.TheoTableColumnBackground
import com.theo.theotable.compose.style.TheoTableDefaults
import com.theo.theotable.compose.style.TheoTableDividerColors
import com.theo.theotable.compose.style.TheoTableStyle
import com.theo.theotable.compose.style.TheoTableTextStyles
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.TableEngine
import com.theo.theotable.core.TableSort

@Composable
fun <T, K: Any> TheoTable(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    rowKey: (T) -> K,
    style: TheoTableStyle,
    modifier: Modifier = Modifier,
    state: TheoTableState<K> = rememberTheoTableState(),
    selectionMode: SelectionMode = SelectionMode.None,
    sortingEnabled: Boolean = true,
    overscrollEnabled: Boolean = false,
    frozenColumnCount: Int = 0,
    columnWidthResolvingMode: TheoTableColumnWidthResolvingMode = TheoTableColumnWidthResolvingMode.Immediate,
    columnWidthLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    rowHeight: TheoTableRowHeight = TheoTableRowHeight.WrapContent(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    cellPadding: PaddingValues = PaddingValues(
        horizontal = TheoTableDefaults.CellHorizontalPadding,
        vertical = TheoTableDefaults.CellVerticalPadding,
    ),
) {
    TheoTable(
        rows = rows,
        columns = columns,
        rowKey = rowKey,
        modifier = modifier,
        state = state,
        selectionMode = selectionMode,
        sortingEnabled = sortingEnabled,
        overscrollEnabled = overscrollEnabled,
        frozenColumnCount = frozenColumnCount,
        columnWidthResolvingMode = columnWidthResolvingMode,
        columnWidthLoadingContent = columnWidthLoadingContent,
        textStyle = style.text.base,
        headerTextStyle = style.text.header,
        cellTextStyle = style.text.cell,
        headerBackground = style.background.header,
        cellBackground = style.background.cell,
        selectedRowBackground = style.background.selectedRow,
        columnBackgrounds = style.background.columns,
        borderColor = style.divider.border,
        dividerColors = style.divider,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        cellPadding = cellPadding,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, K: Any> TheoTable(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    rowKey: (T) -> K,
    modifier: Modifier = Modifier,
    state: TheoTableState<K> = rememberTheoTableState(),
    selectionMode: SelectionMode = SelectionMode.None,
    sortingEnabled: Boolean = true,
    overscrollEnabled: Boolean = false,
    frozenColumnCount: Int = 0,
    columnWidthResolvingMode: TheoTableColumnWidthResolvingMode = TheoTableColumnWidthResolvingMode.Immediate,
    columnWidthLoadingContent: (@Composable BoxScope.() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    headerTextStyle: TextStyle = textStyle,
    cellTextStyle: TextStyle = textStyle,
    headerBackground: Color = TheoTableDefaults.HeaderBackground,
    cellBackground: Color = TheoTableDefaults.CellBackground,
    selectedRowBackground: Color = TheoTableDefaults.SelectedRowBackground,
    columnBackgrounds: Map<String, TheoTableColumnBackground> = emptyMap(),
    borderColor: Color = TheoTableDefaults.BorderColor,
    dividerColors: TheoTableDividerColors = TheoTableDefaults.dividerColors(
        border = borderColor,
        vertical = borderColor,
        horizontal = borderColor,
    ),
    rowHeight: TheoTableRowHeight = TheoTableRowHeight.WrapContent(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    cellPadding: PaddingValues = PaddingValues(
        horizontal = TheoTableDefaults.CellHorizontalPadding,
        vertical = TheoTableDefaults.CellVerticalPadding,
    ),
) {
    val coreColumns = remember(columns) {
        columns.map { it.asCoreColumn() }
    }

    val tableState = state.value
    val effectiveTableState = if(sortingEnabled) {
        tableState
    } else {
        tableState.copy(sort = TableSort())
    }

    val snapshot = remember(rows, effectiveTableState, coreColumns, rowKey) {
        TableEngine(
            columns = coreColumns,
            rowKey = rowKey,
        ).snapshot(
            rows = rows,
            state = effectiveTableState,
        )
    }

    val resolvedColumnWidths = rememberResolvedColumnWidths(
        rows = rows,
        columns = columns,
        cellPadding = cellPadding,
        defaultHeaderTextStyle = headerTextStyle,
        defaultCellTextStyle = cellTextStyle,
        resolvingMode = columnWidthResolvingMode,
    )

    val columnWidths = resolvedColumnWidths.widths

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberLazyListState()

    val columnLayout = remember(columns.size, frozenColumnCount) {
        resolveTheoTableColumnLayout(
            columnCount = columns.size,
            frozenColumnCount = frozenColumnCount,
        )
    }
    val layoutDirection = LocalLayoutDirection.current
    val contentStartPadding = contentPadding.calculateStartPadding(layoutDirection)
    val contentEndPadding = contentPadding.calculateEndPadding(layoutDirection)
    val contentTopPadding = contentPadding.calculateTopPadding()
    val contentBottomPadding = contentPadding.calculateBottomPadding()
    val contentPaddingLayout = remember(columnLayout, contentStartPadding, contentEndPadding) {
        resolveTheoTableContentPaddingLayout(
            columnLayout = columnLayout,
            startPadding = contentStartPadding,
            endPadding = contentEndPadding,
        )
    }

    CompositionLocalProvider(
        LocalTheoTableTextStyles provides TheoTableTextStyles(
            headerTextStyle = headerTextStyle,
            cellTextStyle = cellTextStyle,
        ),
        LocalOverscrollConfiguration provides if(overscrollEnabled) {
            LocalOverscrollConfiguration.current
        } else {
            null
        }
    ) {
        BoxWithConstraints(
            modifier = modifier
                .border(width = 1.dp, color = dividerColors.border),
        ) {
            val frozenWidth = columnWidths.sumRange(columnLayout.frozen)
            val scrollableWidth = columnWidths.sumRange(columnLayout.scrollable)
            val frozenContentWidth = frozenWidth +
                    contentPaddingLayout.frozenStart +
                    contentPaddingLayout.frozenEnd
            val scrollableContentWidth = scrollableWidth +
                    contentPaddingLayout.scrollableStart +
                    contentPaddingLayout.scrollableEnd
            val remainingWidth = maxWidth - frozenContentWidth

            val scrollableViewportWidth = when {
                !columnLayout.hasScrollableColumns -> 0.dp
                remainingWidth <= 0.dp -> 0.dp
                scrollableContentWidth < remainingWidth -> scrollableContentWidth
                else -> remainingWidth
            }

            Column {
                if(contentTopPadding > 0.dp) {
                    Spacer(modifier = Modifier.height(contentTopPadding))
                }

                TheoTableHeader(
                    columns = columns,
                    columnWidths = columnWidths,
                    state = state,
                    sortingEnabled = sortingEnabled,
                    columnLayout = columnLayout,
                    horizontalScrollState = horizontalScrollState,
                    scrollableViewportWidth = scrollableViewportWidth,
                    contentPaddingLayout = contentPaddingLayout,
                    headerBackground = headerBackground,
                    columnBackgrounds = columnBackgrounds,
                    dividerColors = dividerColors,
                    cellPadding = cellPadding,
                )

                LazyColumn(
                    state = verticalScrollState,
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(
                        items = snapshot.rows,
                        key = { index, _ -> snapshot.rowKeys[index] },
                    ) { index, row ->
                        val key = snapshot.rowKeys[index]
                        val selectable = selectionMode != SelectionMode.None
                        val selected = selectable && state.selection.isSelected(key)
                        val isLastRow = index == snapshot.rows.lastIndex

                        TheoTableRow(
                            row = row,
                            columns = columns,
                            columnWidths = columnWidths,
                            columnLayout = columnLayout,
                            horizontalScrollState = horizontalScrollState,
                            rowHeight = rowHeight,
                            scrollableViewportWidth = scrollableViewportWidth,
                            contentPaddingLayout = contentPaddingLayout,
                            selectable = selectable,
                            selected = selected,
                            isLastRow = isLastRow,
                            cellBackground = cellBackground,
                            selectedRowBackground = selectedRowBackground,
                            columnBackgrounds = columnBackgrounds,
                            dividerColors = dividerColors,
                            cellPadding = cellPadding,
                            onClick = {
                                state.toggleSelection(
                                    key = key,
                                    mode = selectionMode,
                                )
                            },
                        )
                    }

                    if(contentBottomPadding > 0.dp) {
                        item {
                            Spacer(modifier = Modifier.height(contentBottomPadding))
                        }
                    }
                }
            }

            val loadingContent = columnWidthLoadingContent

            if(resolvedColumnWidths.isResolving && loadingContent != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
                    content = loadingContent,
                )
            }
        }
    }
}

@Composable
private fun <T, K> TheoTableHeader(
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    state: TheoTableState<K>,
    sortingEnabled: Boolean,
    columnLayout: TheoTableColumnLayout,
    horizontalScrollState: ScrollState,
    scrollableViewportWidth: Dp,
    contentPaddingLayout: TheoTableContentPaddingLayout,
    headerBackground: Color,
    columnBackgrounds: Map<String, TheoTableColumnBackground>,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = TheoTableDefaults.HeaderHeight),
    ) {
        TheoTableHeaderCells(
            columns = columns,
            columnWidths = columnWidths,
            range = columnLayout.frozen,
            startContentPadding = contentPaddingLayout.frozenStart,
            endContentPadding = contentPaddingLayout.frozenEnd,
            state = state,
            sortingEnabled = sortingEnabled,
            headerBackground = headerBackground,
            columnBackgrounds = columnBackgrounds,
            dividerColors = dividerColors,
            cellPadding = cellPadding,
        )

        if(columnLayout.hasScrollableColumns) {
            Row(
                modifier = Modifier
                    .width(scrollableViewportWidth)
                    .horizontalScroll(horizontalScrollState)
            ) {
                TheoTableHeaderCells(
                    columns = columns,
                    columnWidths = columnWidths,
                    range = columnLayout.scrollable,
                    startContentPadding = contentPaddingLayout.scrollableStart,
                    endContentPadding = contentPaddingLayout.scrollableEnd,
                    state = state,
                    sortingEnabled = sortingEnabled,
                    headerBackground = headerBackground,
                    columnBackgrounds = columnBackgrounds,
                    dividerColors = dividerColors,
                    cellPadding = cellPadding,
                )
            }
        }
    }
}

@Composable
private fun <T, K> TheoTableHeaderCells(
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    range: TheoTableColumnRange,
    startContentPadding: Dp,
    endContentPadding: Dp,
    state: TheoTableState<K>,
    sortingEnabled: Boolean,
    headerBackground: Color,
    columnBackgrounds: Map<String, TheoTableColumnBackground>,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    if(startContentPadding > 0.dp) {
        Spacer(modifier = Modifier.width(startContentPadding))
    }

    for(index in range.startIndex until range.endIndex) {
        val column = columns[index]
        val coreColumn = remember(column) { column.asCoreColumn() }
        val sortSpec = state.sort.specs.firstOrNull { it.columnId == column.id }
        val isSortable = sortingEnabled && column.sortable && column.comparator != null
        val hasNextColumn = index < columns.lastIndex

        val headerState = TheoTableHeaderState(
            isSorted = sortingEnabled && sortSpec != null,
            sortDirection = if(sortingEnabled) sortSpec?.direction else null,
            sortable = isSortable,
            sortingEnabled = sortingEnabled,
        )

        val columnBackground = columnBackgrounds[column.id.value]

        Box(
            modifier = Modifier
                .width(columnWidths[index])
                .fillMaxHeight()
                .defaultMinSize(minHeight = TheoTableDefaults.HeaderHeight)
                .background(columnBackground?.header ?: headerBackground)
                .then(
                    Modifier.theoTableDivider(
                        verticalColor = dividerColors.headerVertical,
                        horizontalColor = dividerColors.headerHorizontal,
                        drawVertical = hasNextColumn,
                        drawHorizontal = true,
                    )
                )
                .clickable(enabled = isSortable) {
                    state.toggleSort(coreColumn)
                }
                .padding(cellPadding),
            contentAlignment = column.headerAlignment,
        ) {
            column.header(this, headerState)
        }
    }

    if(endContentPadding > 0.dp) {
        Spacer(modifier = Modifier.width(endContentPadding))
    }
}

@Composable
private fun <T> TheoTableRow(
    row: T,
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    columnLayout: TheoTableColumnLayout,
    horizontalScrollState: ScrollState,
    rowHeight: TheoTableRowHeight,
    scrollableViewportWidth: Dp,
    contentPaddingLayout: TheoTableContentPaddingLayout,
    selectable: Boolean,
    selected: Boolean,
    isLastRow: Boolean,
    cellBackground: Color,
    selectedRowBackground: Color,
    columnBackgrounds: Map<String, TheoTableColumnBackground>,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
    onClick: () -> Unit,
) {
    val rowModifier = when(rowHeight) {
        is TheoTableRowHeight.Fixed -> Modifier.height(rowHeight.value)
        is TheoTableRowHeight.WrapContent -> Modifier
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = rowHeight.min)
    }
    Row(
        modifier = rowModifier.clickable(enabled = selectable, onClick = onClick),
    ) {
        TheoTableRowCells(
            row = row,
            columns = columns,
            columnWidths = columnWidths,
            range = columnLayout.frozen,
            startContentPadding = contentPaddingLayout.frozenStart,
            endContentPadding = contentPaddingLayout.frozenEnd,
            selected = selected,
            isLastRow = isLastRow,
            cellBackground = cellBackground,
            selectedRowBackground = selectedRowBackground,
            columnBackgrounds = columnBackgrounds,
            dividerColors = dividerColors,
            cellPadding = cellPadding,
        )

        if(columnLayout.hasScrollableColumns) {
            Row(
                modifier = Modifier
                    .width(scrollableViewportWidth)
                    .fillMaxHeight()
                    .horizontalScroll(horizontalScrollState),
            ) {
                TheoTableRowCells(
                    row = row,
                    columns = columns,
                    columnWidths = columnWidths,
                    range = columnLayout.scrollable,
                    startContentPadding = contentPaddingLayout.scrollableStart,
                    endContentPadding = contentPaddingLayout.scrollableEnd,
                    selected = selected,
                    isLastRow = isLastRow,
                    cellBackground = cellBackground,
                    selectedRowBackground = selectedRowBackground,
                    columnBackgrounds = columnBackgrounds,
                    dividerColors = dividerColors,
                    cellPadding = cellPadding,
                )
            }
        }
    }
}

@Composable
private fun <T> TheoTableRowCells(
    row: T,
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    range: TheoTableColumnRange,
    startContentPadding: Dp,
    endContentPadding: Dp,
    selected: Boolean,
    isLastRow: Boolean,
    cellBackground: Color,
    selectedRowBackground: Color,
    columnBackgrounds: Map<String, TheoTableColumnBackground>,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    if(startContentPadding > 0.dp) {
        Spacer(modifier = Modifier.width(startContentPadding))
    }

    for(index in range.startIndex until range.endIndex) {
        val column = columns[index]
        val hasNextColumn = index < columns.lastIndex
        val columnBackground = columnBackgrounds[column.id.value]
        val resolvedCellBackground = if(selected) {
            selectedRowBackground
        } else {
            columnBackground?.cell ?: cellBackground
        }

        Box(
            modifier = Modifier
                .width(columnWidths[index])
                .fillMaxHeight()
                .background(resolvedCellBackground)
                .then(
                    Modifier.theoTableDivider(
                        verticalColor = dividerColors.cellVertical,
                        horizontalColor = dividerColors.cellHorizontal,
                        drawVertical = hasNextColumn,
                        drawHorizontal = !isLastRow
                    )
                )
                .padding(cellPadding),
            contentAlignment = column.cellAlignment,
        ) {
            column.cell(this, row)
        }
    }

    if(endContentPadding > 0.dp) {
        Spacer(modifier = Modifier.width(endContentPadding))
    }
}

private fun Modifier.theoTableDivider(
    verticalColor: Color,
    horizontalColor: Color,
    drawVertical: Boolean,
    drawHorizontal: Boolean,
    width: Dp = 0.5.dp,
): Modifier {
    return drawBehind {
        val strokeWidth = width.toPx()

        if(drawVertical) {
            drawLine(
                color = verticalColor,
                start = Offset(size.width - strokeWidth / 2f, 0f),
                end = Offset(size.width - strokeWidth / 2f, size.height),
                strokeWidth = strokeWidth,
            )
        }

        if(drawHorizontal) {
            drawLine(
                color = horizontalColor,
                start = Offset(0f, size.height - strokeWidth / 2f),
                end = Offset(size.width, size.height - strokeWidth / 2f),
                strokeWidth = strokeWidth,
            )
        }
    }
}

private fun List<Dp>.sumRange(range: TheoTableColumnRange): Dp {
    var sum = 0.dp

    for(index in range.startIndex until range.endIndex) {
        sum += this[index]
    }

    return sum
}
