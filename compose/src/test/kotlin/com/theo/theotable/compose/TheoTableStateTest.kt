package com.theo.theotable.compose

import androidx.compose.runtime.saveable.SaverScope
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.core.SortDirection
import com.theo.theotable.core.SortSpec
import com.theo.theotable.core.TableColumnId
import com.theo.theotable.core.TableSelection
import com.theo.theotable.core.TableSort
import com.theo.theotable.core.TableState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class TheoTableStateTest {
    private val saveableScope = object: SaverScope {
        override fun canBeSaved(value: Any): Boolean = true
    }

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
        val unsaveableScope = object: SaverScope {
            override fun canBeSaved(value: Any): Boolean = value !is UnsaveableKey
        }
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

    private data class UnsaveableKey(val value: Int)
}
