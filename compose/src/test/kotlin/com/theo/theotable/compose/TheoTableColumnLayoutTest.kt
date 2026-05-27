package com.theo.theotable.compose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TheoTableColumnLayoutTest {
    @Test
    fun resolve_clampsNegativeFrozenColumnCount() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 3,
            frozenColumnCount = -1,
        )

        assertEquals(TheoTableColumnRange(0, 0), layout.frozen)
        assertEquals(TheoTableColumnRange(0, 3), layout.scrollable)
        assertFalse(layout.hasFrozenColumns)
        assertTrue(layout.hasScrollableColumns)
    }

    @Test
    fun resolve_splitsFrozenAndScrollableColumns() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 5,
            frozenColumnCount = 2,
        )

        assertEquals(TheoTableColumnRange(0, 2), layout.frozen)
        assertEquals(TheoTableColumnRange(2, 5), layout.scrollable)
        assertTrue(layout.hasFrozenColumns)
        assertTrue(layout.hasScrollableColumns)
    }

    @Test
    fun resolve_clampsFrozenColumnCountToColumnCount() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 3,
            frozenColumnCount = 10,
        )

        assertEquals(TheoTableColumnRange(0, 3), layout.frozen)
        assertEquals(TheoTableColumnRange(3, 3), layout.scrollable)
        assertTrue(layout.hasFrozenColumns)
        assertFalse(layout.hasScrollableColumns)
    }

    @Test
    fun resolve_handlesEmptyColumns() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 0,
            frozenColumnCount = 1,
        )

        assertEquals(TheoTableColumnRange(0, 0), layout.frozen)
        assertEquals(TheoTableColumnRange(0, 0), layout.scrollable)
        assertFalse(layout.hasFrozenColumns)
        assertFalse(layout.hasScrollableColumns)
    }

    @Test
    fun resolve_rejectsNegativeColumnCount() {
        assertThrows(IllegalArgumentException::class.java) {
            resolveTheoTableColumnLayout(
                columnCount = -1,
                frozenColumnCount = 0,
            )
        }
    }
}
