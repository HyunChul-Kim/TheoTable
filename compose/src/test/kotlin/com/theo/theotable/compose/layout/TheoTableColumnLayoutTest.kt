package com.theo.theotable.compose.layout

import androidx.compose.ui.unit.dp
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

    @Test
    fun resolveContentPadding_placesBothSidesInScrollableAreaWhenThereAreNoFrozenColumns() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 3,
            frozenColumnCount = 0,
        )

        val paddingLayout = resolveTheoTableContentPaddingLayout(
            columnLayout = layout,
            startPadding = 12.dp,
            endPadding = 20.dp,
        )

        assertEquals(0.dp, paddingLayout.frozenStart)
        assertEquals(0.dp, paddingLayout.frozenEnd)
        assertEquals(12.dp, paddingLayout.scrollableStart)
        assertEquals(20.dp, paddingLayout.scrollableEnd)
    }

    @Test
    fun resolveContentPadding_splitsPaddingBetweenFrozenAndScrollableAreas() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 5,
            frozenColumnCount = 2,
        )

        val paddingLayout = resolveTheoTableContentPaddingLayout(
            columnLayout = layout,
            startPadding = 12.dp,
            endPadding = 20.dp,
        )

        assertEquals(12.dp, paddingLayout.frozenStart)
        assertEquals(0.dp, paddingLayout.frozenEnd)
        assertEquals(0.dp, paddingLayout.scrollableStart)
        assertEquals(20.dp, paddingLayout.scrollableEnd)
    }

    @Test
    fun resolveContentPadding_placesBothSidesInFrozenAreaWhenAllColumnsAreFrozen() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 3,
            frozenColumnCount = 3,
        )

        val paddingLayout = resolveTheoTableContentPaddingLayout(
            columnLayout = layout,
            startPadding = 12.dp,
            endPadding = 20.dp,
        )

        assertEquals(12.dp, paddingLayout.frozenStart)
        assertEquals(20.dp, paddingLayout.frozenEnd)
        assertEquals(0.dp, paddingLayout.scrollableStart)
        assertEquals(0.dp, paddingLayout.scrollableEnd)
    }

    @Test
    fun resolveContentPadding_ignoresHorizontalPaddingWhenThereAreNoColumns() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 0,
            frozenColumnCount = 0,
        )

        val paddingLayout = resolveTheoTableContentPaddingLayout(
            columnLayout = layout,
            startPadding = 12.dp,
            endPadding = 20.dp,
        )

        assertEquals(0.dp, paddingLayout.frozenStart)
        assertEquals(0.dp, paddingLayout.frozenEnd)
        assertEquals(0.dp, paddingLayout.scrollableStart)
        assertEquals(0.dp, paddingLayout.scrollableEnd)
    }

    @Test
    fun resolveContentPadding_rejectsNegativePadding() {
        val layout = resolveTheoTableColumnLayout(
            columnCount = 1,
            frozenColumnCount = 0,
        )

        assertThrows(IllegalArgumentException::class.java) {
            resolveTheoTableContentPaddingLayout(
                columnLayout = layout,
                startPadding = (-1).dp,
                endPadding = 0.dp,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            resolveTheoTableContentPaddingLayout(
                columnLayout = layout,
                startPadding = 0.dp,
                endPadding = (-1).dp,
            )
        }
    }
}