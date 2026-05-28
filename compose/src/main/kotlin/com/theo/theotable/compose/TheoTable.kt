package com.theo.theotable.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.TableEngine
import com.theo.theotable.core.TableSort

@Composable
fun <T, K> TheoTable(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    rowKey: (T) -> K,
    style: TheoTableStyle,
    modifier: Modifier = Modifier,
    state: TheoTableState<K> = rememberTheoTableState(),
    selectionMode: SelectionMode = SelectionMode.None,
    sortingEnabled: Boolean = true,
    verticalScrollEnabled: Boolean = true,
    overscrollEnabled: Boolean = false,
    frozenColumnCount: Int = 0,
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
        verticalScrollEnabled = verticalScrollEnabled,
        overscrollEnabled = overscrollEnabled,
        frozenColumnCount = frozenColumnCount,
        textStyle = style.text.base,
        headerTextStyle = style.text.header,
        cellTextStyle = style.text.cell,
        headerBackground = style.background.header,
        cellBackground = style.background.cell,
        selectedRowBackground = style.background.selectedRow,
        borderColor = style.divider.border,
        dividerColors = style.divider,
        cellPadding = cellPadding,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, K> TheoTable(
    rows: List<T>,
    columns: List<TheoTableColumn<T>>,
    rowKey: (T) -> K,
    modifier: Modifier = Modifier,
    state: TheoTableState<K> = rememberTheoTableState(),
    selectionMode: SelectionMode = SelectionMode.None,
    sortingEnabled: Boolean = true,
    verticalScrollEnabled: Boolean = true,
    overscrollEnabled: Boolean = false,
    frozenColumnCount: Int = 0,
    textStyle: TextStyle = TextStyle.Default,
    headerTextStyle: TextStyle = textStyle,
    cellTextStyle: TextStyle = textStyle,
    headerBackground: Color = TheoTableDefaults.HeaderBackground,
    cellBackground: Color = TheoTableDefaults.CellBackground,
    selectedRowBackground: Color = TheoTableDefaults.SelectedRowBackground,
    borderColor: Color = TheoTableDefaults.BorderColor,
    dividerColors: TheoTableDividerColors = TheoTableDefaults.dividerColors(
        border = borderColor,
        vertical = borderColor,
        horizontal = borderColor,
    ),
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

    val columnWidths = rememberResolvedColumnWidths(
        rows = rows,
        columns = columns,
        cellPadding = cellPadding,
        defaultHeaderTextStyle = headerTextStyle,
        defaultCellTextStyle = cellTextStyle,
    )

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    val columnLayout = remember(columns.size, frozenColumnCount) {
        resolveTheoTableColumnLayout(
            columnCount = columns.size,
            frozenColumnCount = frozenColumnCount,
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
            val remainingWidth = maxWidth - frozenWidth

            val scrollableViewportWidth = when {
                !columnLayout.hasScrollableColumns -> 0.dp
                remainingWidth <= 0.dp -> 0.dp
                scrollableWidth < remainingWidth -> scrollableWidth
                else -> remainingWidth
            }

            Column {
                TheoTableHeader(
                    columns = columns,
                    columnWidths = columnWidths,
                    state = state,
                    sortingEnabled = sortingEnabled,
                    columnLayout = columnLayout,
                    horizontalScrollState = horizontalScrollState,
                    scrollableViewportWidth = scrollableViewportWidth,
                    headerBackground = headerBackground,
                    dividerColors = dividerColors,
                    cellPadding = cellPadding,
                )

                Column(
                    modifier = if(verticalScrollEnabled) {
                        Modifier.verticalScroll(verticalScrollState)
                    } else {
                        Modifier
                    },
                ) {
                    snapshot.rows.forEachIndexed { index, row ->
                        val key = snapshot.rowKeys[index]
                        val selectable = selectionMode != SelectionMode.None
                        val selected = selectable && state.selection.isSelected(key)

                        TheoTableRow(
                            row = row,
                            columns = columns,
                            columnWidths = columnWidths,
                            columnLayout = columnLayout,
                            horizontalScrollState = horizontalScrollState,
                            scrollableViewportWidth = scrollableViewportWidth,
                            selectable = selectable,
                            selected = selected,
                            cellBackground = cellBackground,
                            selectedRowBackground = selectedRowBackground,
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
                }
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
    headerBackground: Color,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    Row(
        modifier = Modifier
            .height(TheoTableDefaults.HeaderHeight)
            .background(headerBackground),
    ) {
        TheoTableHeaderCells(
            columns = columns,
            columnWidths = columnWidths,
            range = columnLayout.frozen,
            state = state,
            sortingEnabled = sortingEnabled,
            headerBackground = headerBackground,
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
                    state = state,
                    sortingEnabled = sortingEnabled,
                    headerBackground = headerBackground,
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
    state: TheoTableState<K>,
    sortingEnabled: Boolean,
    headerBackground: Color,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    for(index in range.startIndex until range.endIndex) {
        val column = columns[index]
        val coreColumn = remember(column) { column.asCoreColumn() }
        val sortSpec = state.sort.specs.firstOrNull { it.columnId == column.id }
        val isSortable = sortingEnabled && column.sortable && column.comparator != null

        val headerState = TheoTableHeaderState(
            isSorted = sortingEnabled && sortSpec != null,
            sortDirection = if(sortingEnabled) sortSpec?.direction else null,
            sortable = isSortable,
            sortingEnabled = sortingEnabled,
        )

        Box(
            modifier = Modifier
                .width(columnWidths[index])
                .height(TheoTableDefaults.HeaderHeight)
                .background(column.headerBackground ?: headerBackground)
                .then(
                    Modifier.theoTableDivider(
                        verticalColor = dividerColors.headerVertical,
                        horizontalColor = dividerColors.headerHorizontal,
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
}

@Composable
private fun <T> TheoTableRow(
    row: T,
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    columnLayout: TheoTableColumnLayout,
    horizontalScrollState: ScrollState,
    scrollableViewportWidth: Dp,
    selectable: Boolean,
    selected: Boolean,
    cellBackground: Color,
    selectedRowBackground: Color,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = TheoTableDefaults.RowMinHeight)
            .background(if(selected) selectedRowBackground else Color.Transparent)
            .clickable(enabled = selectable, onClick = onClick),
    ) {
        TheoTableRowCells(
            row = row,
            columns = columns,
            columnWidths = columnWidths,
            range = columnLayout.frozen,
            selected = selected,
            cellBackground = cellBackground,
            selectedRowBackground = selectedRowBackground,
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
                    selected = selected,
                    cellBackground = cellBackground,
                    selectedRowBackground = selectedRowBackground,
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
    selected: Boolean,
    cellBackground: Color,
    selectedRowBackground: Color,
    dividerColors: TheoTableDividerColors,
    cellPadding: PaddingValues,
) {
    for(index in range.startIndex until range.endIndex) {
        val column = columns[index]
        val resolvedCellBackground = if(selected) {
            selectedRowBackground
        } else {
            column.cellBackground ?: cellBackground
        }

        Box(
            modifier = Modifier
                .width(columnWidths[index])
                .fillMaxHeight()
                .defaultMinSize(minHeight = TheoTableDefaults.RowMinHeight)
                .background(resolvedCellBackground)
                .then(
                    Modifier.theoTableDivider(
                        verticalColor = dividerColors.cellVertical,
                        horizontalColor = dividerColors.cellHorizontal,
                    )
                )
                .padding(cellPadding),
            contentAlignment = column.cellAlignment,
        ) {
            column.cell(this, row)
        }
    }
}

private fun Modifier.theoTableDivider(
    verticalColor: Color,
    horizontalColor: Color,
    width: Dp = 0.5.dp,
): Modifier {
    return drawBehind {
        val strokeWidth = width.toPx()

        drawLine(
            color = verticalColor,
            start = Offset(size.width - strokeWidth / 2f, 0f),
            end = Offset(size.width - strokeWidth / 2f, size.height),
            strokeWidth = strokeWidth,
        )

        drawLine(
            color = horizontalColor,
            start = Offset(0f, size.height - strokeWidth / 2f),
            end = Offset(size.width, size.height - strokeWidth / 2f),
            strokeWidth = strokeWidth,
        )
    }
}

private fun List<Dp>.sumRange(range: TheoTableColumnRange): Dp {
    var sum = 0.dp

    for(index in range.startIndex until range.endIndex) {
        sum += this[index]
    }

    return sum
}
