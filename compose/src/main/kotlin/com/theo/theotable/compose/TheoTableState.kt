package com.theo.theotable.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.TableColumn
import com.theo.theotable.core.TableSelection
import com.theo.theotable.core.TableSort
import com.theo.theotable.core.TableState

@Stable
class TheoTableState<K> internal constructor(
    initialState: TableState<K>,
) {
    var value: TableState<K> by mutableStateOf(initialState)
        private set

    val sort: TableSort
        get() = value.sort

    val selection: TableSelection<K>
        get() = value.selection

    fun setSort(sort: TableSort) {
        value = value.copy(sort = sort)
    }

    fun toggleSort(column: TableColumn<*>, multiSort: Boolean = false) {
        setSort(sort.toggle(column = column, multiSort = multiSort))
    }

    fun clearSelection() {
        value = value.copy(selection = selection.clear())
    }

    fun select(key: K, mode: SelectionMode) {
        value = value.copy(selection = selection.select(key, mode))
    }

    fun toggleSelection(key: K, mode: SelectionMode) {
        value = value.copy(selection = selection.toggle(key, mode))
    }

    fun selectRange(targetKey: K, orderedKeys: List<K>, mode: SelectionMode) {
        value = value.copy(
            selection = selection.selectRange(
                targetKey = targetKey,
                orderedKeys = orderedKeys,
                mode = mode,
            )
        )
    }
}

@Composable
fun <K> rememberTheoTableState(
    initialState: TableState<K> = TableState(),
): TheoTableState<K> {
    return remember { TheoTableState(initialState) }
}