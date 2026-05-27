package com.theo.theotable.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
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
    modifier: Modifier = Modifier,
    state: TheoTableState<K> = rememberTheoTableState(),
    selectionMode: SelectionMode = SelectionMode.None,
    sortingEnabled: Boolean = true,
    verticalScrollEnabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    headerTextStyle: TextStyle = textStyle,
    cellTextStyle: TextStyle = textStyle,
    headerBackground: Color = TheoTableDefaults.HeaderBackground,
    cellBackground: Color = TheoTableDefaults.CellBackground,
    selectedRowBackground: Color = TheoTableDefaults.SelectedRowBackground,
    borderColor: Color = TheoTableDefaults.BorderColor,
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

    CompositionLocalProvider(
        LocalTheoTableTextStyles provides TheoTableTextStyles(
            headerTextStyle = headerTextStyle,
            cellTextStyle = cellTextStyle,
        )
    ) {
        Column(
            modifier = modifier
                .border(width = 1.dp, color = borderColor)
                .horizontalScroll(horizontalScrollState),
        ) {
            TheoTableHeader(
                columns = columns,
                columnWidths = columnWidths,
                state = state,
                sortingEnabled = sortingEnabled,
                headerBackground = headerBackground,
                borderColor = borderColor,
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
                        selectable = selectable,
                        selected = selected,
                        cellBackground = cellBackground,
                        selectedRowBackground = selectedRowBackground,
                        borderColor = borderColor,
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

@Composable
private fun <T, K> TheoTableHeader(
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    state: TheoTableState<K>,
    sortingEnabled: Boolean,
    headerBackground: Color,
    borderColor: Color,
    cellPadding: PaddingValues,
) {
    Row(
        modifier = Modifier
            .height(TheoTableDefaults.HeaderHeight)
            .background(headerBackground),
    ) {
        columns.forEachIndexed { index, column ->
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
                    .border(width = 0.5.dp, color = borderColor)
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
}

@Composable
private fun <T> TheoTableRow(
    row: T,
    columns: List<TheoTableColumn<T>>,
    columnWidths: List<Dp>,
    selectable: Boolean,
    selected: Boolean,
    cellBackground: Color,
    selectedRowBackground: Color,
    borderColor: Color,
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
        columns.forEachIndexed { index, column ->
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
                    .border(width = 0.5.dp, color = borderColor)
                    .padding(cellPadding),
                contentAlignment = column.cellAlignment,
            ) {
                column.cell(this, row)
            }
        }
    }
}