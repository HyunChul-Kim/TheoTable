package com.theo.theotable.compose.state

import androidx.compose.runtime.saveable.SaverScope
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.SortDirection
import com.theo.theotable.core.SortSpec
import com.theo.theotable.core.TableColumnId
import com.theo.theotable.core.TableSelection
import com.theo.theotable.core.TableSort
import com.theo.theotable.core.TableState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TheoTableStateTest {
    private val saveableScope = SaverScope { true }

    @Test
    fun saver_restoresSelection() {
        val state = TheoTableState(TableState<Int>())
        state.toggleSelection(2, SelectionMode.Multiple)
        state.toggleSelection(3, SelectionMode.Multiple)

        val restored = saveAndRestore(state)

        assertNotNull(restored)
        assertEquals(setOf(2, 3), restored?.selection?.selectedKeys)
        assertEquals(3, restored?.selection?.anchorKey)
    }

    @Test
    fun saver_restoresSort() {
        val state = TheoTableState(
            TableState<Int>(
                sort = TableSort(
                    specs = listOf(
                        SortSpec(
                            columnId = TableColumnId("name"),
                            direction = SortDirection.Descending,
                        )
                    )
                )
            )
        )

        val restored = saveAndRestore(state)

        assertNotNull(restored)
        assertEquals(state.sort, restored?.sort)
    }

    @Test
    fun saver_returnsNullForUnsaveableSelectedKey() {
        val unsaveableScope = SaverScope { value -> value !is UnsaveableKey }
        val saver = theoTableStateSaver<UnsaveableKey>()
        val state = TheoTableState(
            TableState(
                selection = TableSelection(
                    selectedKeys = setOf(UnsaveableKey(1)),
                    anchorKey = UnsaveableKey(1),
                )
            )
        )

        val saved = with(saver) {
            unsaveableScope.save(state)
        }

        assertNull(saved)
    }

    private fun <K : Any> saveAndRestore(state: TheoTableState<K>): TheoTableState<K>? {
        val saver = theoTableStateSaver<K>()
        val saved = with(saver) {
            saveableScope.save(state)
        } ?: return null

        return saver.restore(saved)
    }

    @Test
    fun columnWidthResolutionStatus_defaultsToPending() {
        val state = TheoTableState(TableState<Int>())

        assertEquals(
            TheoTableColumnWidthResolutionStatus.Pending,
            state.columnWidthResolutionStatus,
        )
        assertFalse(state.isColumnWidthResolving)
        assertFalse(state.isColumnWidthResolved)
    }

    @Test
    fun columnWidthResolutionStatus_isNotSaved() {
        val state = TheoTableState(TableState<Int>())
        state.setColumnWidthResolutionStatus(
            TheoTableColumnWidthResolutionStatus.Resolved
        )

        val restored = saveAndRestore(state)

        assertNotNull(restored)
        assertEquals(
            TheoTableColumnWidthResolutionStatus.Pending,
            restored?.columnWidthResolutionStatus,
        )
    }

    @Test
    fun columnWidthResolutionStatus_updatesDerivedProperties() {
        val state = TheoTableState(TableState<Int>())

        state.setColumnWidthResolutionStatus(
            TheoTableColumnWidthResolutionStatus.Resolving
        )

        assertTrue(state.isColumnWidthResolving)
        assertFalse(state.isColumnWidthResolved)

        state.setColumnWidthResolutionStatus(
            TheoTableColumnWidthResolutionStatus.Resolved
        )

        assertFalse(state.isColumnWidthResolving)
        assertTrue(state.isColumnWidthResolved)
    }

    @Test
    fun value_reflectsSortAndSelectionUpdates() {
        val state = TheoTableState(TableState<Int>())
        val sort = TableSort(
            specs = listOf(
                SortSpec(
                    columnId = TableColumnId("name"),
                    direction = SortDirection.Ascending,
                )
            )
        )

        state.setSort(sort)
        state.toggleSelection(1, SelectionMode.Multiple)

        assertEquals(sort, state.value.sort)
        assertEquals(setOf(1), state.value.selection.selectedKeys)
    }

    private data class UnsaveableKey(val value: Int)
}
