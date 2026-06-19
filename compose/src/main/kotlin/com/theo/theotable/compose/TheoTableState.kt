package com.theo.theotable.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.SortDirection
import com.theo.theotable.core.SortSpec
import com.theo.theotable.core.TableColumn
import com.theo.theotable.core.TableColumnId
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
fun <K : Any> rememberTheoTableState(
    initialState: TableState<K> = TableState(),
): TheoTableState<K> {
    return rememberSaveable(saver = theoTableStateSaver()) {
        TheoTableState(initialState)
    }
}

internal fun <K : Any> theoTableStateSaver(): Saver<TheoTableState<K>, Any> {
    return Saver(
        save = { state -> saveTheoTableState(state) },
        restore = { saved -> restoreTheoTableState(saved) },
    )
}

private fun <K : Any> SaverScope.saveTheoTableState(
    state: TheoTableState<K>,
): Any? {
    val selectedKeys = state.selection.selectedKeys.toList()
    val anchorKey = state.selection.anchorKey

    if(!selectedKeys.all { canBeSaved(it) }) return null
    if(anchorKey != null && !canBeSaved(anchorKey)) return null

    return listOf(
        state.sort.specs.map { it.columnId.value },
        state.sort.specs.map { it.direction.name },
        selectedKeys,
        anchorKey,
    )
}

@Suppress("UNCHECKED_CAST")
private fun <K : Any> restoreTheoTableState(saved: Any): TheoTableState<K>? {
    val values = saved as? List<*> ?: return null
    val columnIds = values.getOrNull(0) as? List<*> ?: emptyList<Any?>()
    val directions = values.getOrNull(1) as? List<*> ?: emptyList<Any?>()
    val selectedKeys = values.getOrNull(2) as? List<*> ?: emptyList<Any?>()

    val specs = columnIds.zip(directions).mapNotNull { (columnId, direction) ->
        val columnIdValue = columnId as? String ?: return@mapNotNull null
        val directionName = direction as? String ?: return@mapNotNull null
        val sortDirection = runCatching {
            SortDirection.valueOf(directionName)
        }.getOrNull() ?: return@mapNotNull null

        SortSpec(
            columnId = TableColumnId(columnIdValue),
            direction = sortDirection,
        )
    }

    return TheoTableState(
        TableState(
            sort = TableSort(specs),
            selection = TableSelection(
                selectedKeys = selectedKeys.mapNotNull { it as? K }.toSet(),
                anchorKey = values.getOrNull(3) as? K,
            ),
        )
    )
}
