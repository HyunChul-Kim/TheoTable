package com.theo.theotable.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TableSelectionTest {
    @Test
    fun select_noneModeClearsSelection() {
        val selection = TableSelection(selectedKeys = setOf(1), anchorKey = 1)
            .select(2, SelectionMode.None)

        assertTrue(selection.selectedKeys.isEmpty())
        assertEquals(null, selection.anchorKey)
    }

    @Test
    fun select_singleModeReplacesPreviousSelection() {
        val selection = TableSelection(selectedKeys = setOf(1), anchorKey = 1)
            .select(2, SelectionMode.Single)

        assertEquals(setOf(2), selection.selectedKeys)
        assertEquals(2, selection.anchorKey)
    }

    @Test
    fun toggle_multipleModeAddsAndRemovesKey() {
        val selected = TableSelection<Int>()
            .toggle(1, SelectionMode.Multiple)

        val deselected = selected.toggle(1, SelectionMode.Multiple)

        assertTrue(selected.isSelected(1))
        assertFalse(deselected.isSelected(1))
        assertEquals(1, deselected.anchorKey)
    }

    @Test
    fun selectRange_multipleModeSelectsKeysBetweenAnchorAndTarget() {
        val selection = TableSelection<Int>()
            .select(2, SelectionMode.Multiple)
            .selectRange(
                targetKey = 5,
                orderedKeys = listOf(1, 2, 3, 4, 5, 6),
                mode = SelectionMode.Multiple,
            )

        assertEquals(setOf(2, 3, 4, 5), selection.selectedKeys)
        assertEquals(2, selection.anchorKey)
    }
}
