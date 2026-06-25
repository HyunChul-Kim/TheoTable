package com.theo.theotable.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theo.theotable.core.SortDirection
import com.theo.theotable.core.TableColumn
import com.theo.theotable.core.TableColumnId

@Immutable
class TheoTableColumn<T>(
    val id: TableColumnId,
    val width: TheoTableColumnWidth = TheoTableColumnWidth.Fixed(160.dp),
    val headerWidthHint: TheoTableWidthHint? = null,
    val widthHint: ((T) -> TheoTableWidthHint)? = null,
    val comparator: Comparator<T>? = null,
    val sortable: Boolean = comparator != null,
    val defaultSortDirection: SortDirection = SortDirection.Ascending,
    val headerAlignment: Alignment = Alignment.CenterStart,
    val cellAlignment: Alignment = Alignment.CenterStart,
    val headerBackground: Color? = null,
    val cellBackground: Color? = null,
    val header: @Composable BoxScope.(state: TheoTableHeaderState) -> Unit,
    val cell: @Composable BoxScope.(row: T) -> Unit,
) {

    internal fun asCoreColumn(): TableColumn<T> {
        return TableColumn(
            id = id,
            comparator = comparator.takeIf { sortable },
            defaultSortDirection = defaultSortDirection,
        )
    }
}

fun <T> theoTableColumn(
    id: String,
    width: TheoTableColumnWidth = TheoTableColumnWidth.Fixed(160.dp),
    headerWidthHint: TheoTableWidthHint? = null,
    widthHint: ((T) -> TheoTableWidthHint)? = null,
    comparator: Comparator<T>? = null,
    sortable: Boolean = comparator != null,
    defaultSortDirection: SortDirection = SortDirection.Ascending,
    headerAlignment: Alignment = Alignment.CenterStart,
    cellAlignment: Alignment = Alignment.CenterStart,
    headerBackground: Color? = null,
    cellBackground: Color? = null,
    header: @Composable BoxScope.(state: TheoTableHeaderState) -> Unit,
    cell: @Composable BoxScope.(row: T) -> Unit,
): TheoTableColumn<T> {
    return TheoTableColumn(
        id = TableColumnId(id),
        width = width,
        headerWidthHint = headerWidthHint,
        widthHint = widthHint,
        comparator = comparator,
        sortable = sortable,
        defaultSortDirection = defaultSortDirection,
        headerAlignment = headerAlignment,
        cellAlignment = cellAlignment,
        headerBackground = headerBackground,
        cellBackground = cellBackground,
        header = header,
        cell = cell,
    )
}

fun <T> theoTextColumn(
    id: String,
    title: String,
    value: (T) -> String,
    width: TheoTableColumnWidth = TheoTableColumnWidth.Content(),
    comparator: Comparator<T>? = null,
    sortable: Boolean = comparator != null,
    defaultSortDirection: SortDirection = SortDirection.Ascending,
    headerAlignment: Alignment = Alignment.CenterStart,
    cellAlignment: Alignment = Alignment.CenterStart,
    headerTextStyle: TextStyle? = null,
    cellTextStyle: TextStyle? = null,
    headerBackground: Color? = null,
    cellBackground: Color? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
): TheoTableColumn<T> {
    return theoTableColumn(
        id = id,
        width = width,
        headerWidthHint = TheoTableWidthHint.Text(
            value = title,
            style = headerTextStyle,
            maxLines = maxLines,
            overflow = overflow,
            softWrap = maxLines > 1,
        ),
        widthHint = { row ->
            TheoTableWidthHint.Text(
                value = value(row),
                style = cellTextStyle,
                maxLines = maxLines,
                overflow = overflow,
                softWrap = maxLines > 1,
            )
        },
        comparator = comparator,
        sortable = sortable,
        defaultSortDirection = defaultSortDirection,
        headerAlignment = headerAlignment,
        cellAlignment = cellAlignment,
        headerBackground = headerBackground,
        cellBackground = cellBackground,
        header = {
            val tableTextStyles = LocalTheoTableTextStyles.current

            BasicText(
                text = title,
                style = headerTextStyle ?: tableTextStyles.headerTextStyle,
                maxLines = maxLines,
                overflow = overflow,
                softWrap = maxLines > 1,
            )
        },
        cell = { row ->
            val tableTextStyles = LocalTheoTableTextStyles.current

            BasicText(
                text = value(row),
                style = cellTextStyle ?: tableTextStyles.cellTextStyle,
                maxLines = maxLines,
                overflow = overflow,
                softWrap = maxLines > 1,
            )
        },
    )
}
