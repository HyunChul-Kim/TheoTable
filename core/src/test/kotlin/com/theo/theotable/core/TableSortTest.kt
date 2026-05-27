package com.theo.theotable.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TableSortTest {
    @Test
    fun toggle_cyclesThroughDefaultDirectionReverseAndCleared() {
        val column = TableColumn<String>(
            id = TableColumnId("name"),
            comparator = compareBy { it },
        )

        val ascending = TableSort().toggle(column)
        val descending = ascending.toggle(column)
        val cleared = descending.toggle(column)

        assertEquals(listOf(SortSpec(column.id, SortDirection.Ascending)), ascending.specs)
        assertEquals(listOf(SortSpec(column.id, SortDirection.Descending)), descending.specs)
        assertTrue(cleared.specs.isEmpty())
    }

    @Test
    fun toggle_startsWithColumnDefaultDirection() {
        val column = TableColumn<String>(
            id = TableColumnId("name"),
            comparator = compareBy { it },
            defaultSortDirection = SortDirection.Descending,
        )

        val descending = TableSort().toggle(column)
        val ascending = descending.toggle(column)

        assertEquals(listOf(SortSpec(column.id, SortDirection.Descending)), descending.specs)
        assertEquals(listOf(SortSpec(column.id, SortDirection.Ascending)), ascending.specs)
    }

    @Test
    fun toggle_replacesExistingSortWhenMultiSortIsDisabled() {
        val nameColumn = TableColumn<String>(
            id = TableColumnId("name"),
            comparator = compareBy { it },
        )
        val priceColumn = TableColumn<String>(
            id = TableColumnId("price"),
            comparator = compareBy { it },
        )

        val sort = TableSort()
            .toggle(nameColumn)
            .toggle(priceColumn)

        assertEquals(listOf(SortSpec(priceColumn.id, SortDirection.Ascending)), sort.specs)
    }

    @Test
    fun toggle_keepsExistingSortWhenMultiSortIsEnabled() {
        val nameColumn = TableColumn<String>(
            id = TableColumnId("name"),
            comparator = compareBy { it },
        )
        val priceColumn = TableColumn<String>(
            id = TableColumnId("price"),
            comparator = compareBy { it },
        )

        val sort = TableSort()
            .toggle(nameColumn)
            .toggle(priceColumn, multiSort = true)

        assertEquals(
            listOf(
                SortSpec(nameColumn.id, SortDirection.Ascending),
                SortSpec(priceColumn.id, SortDirection.Ascending),
            ),
            sort.specs,
        )
    }
}
