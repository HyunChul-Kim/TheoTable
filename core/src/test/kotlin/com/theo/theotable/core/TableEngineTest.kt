package com.theo.theotable.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class TableEngineTest {
    private data class Dessert(
        val id: Int,
        val name: String,
        val price: Int,
    )

    private val rows = listOf(
        Dessert(1, "Cupcake", 3500),
        Dessert(2, "Donut", 2800),
        Dessert(3, "Eclair", 4200),
        Dessert(4, "Froyo", 3900),
        Dessert(5, "Gingerbread", 3200),
    )

    @Test
    fun snapshot_preservesInputOrderWhenSortIsEmpty() {
        val engine = TableEngine(
            columns = listOf(
                TableColumn(
                    id = TableColumnId("price"),
                    comparator = compareBy<Dessert> { it.price },
                ),
            ),
            rowKey = Dessert::id,
        )

        val snapshot = engine.snapshot(rows)

        assertSame(rows, snapshot.rows)
        assertEquals(listOf(1, 2, 3, 4, 5), snapshot.rowKeys)
    }

    @Test
    fun snapshot_sortsRowsWithRequestedDirection() {
        val priceColumn = TableColumn(
            id = TableColumnId("price"),
            comparator = compareBy<Dessert> { it.price },
        )
        val engine = TableEngine(
            columns = listOf(priceColumn),
            rowKey = Dessert::id,
        )

        val snapshot = engine.snapshot(
            rows = rows,
            state = TableState(
                sort = TableSort(
                    specs = listOf(SortSpec(priceColumn.id, SortDirection.Descending)),
                ),
            ),
        )

        assertEquals(listOf(3, 4, 1, 5, 2), snapshot.rowKeys)
    }

    @Test
    fun constructor_rejectsDuplicateColumnIds() {
        val id = TableColumnId("name")

        assertThrows(IllegalArgumentException::class.java) {
            TableEngine<String, String>(
                columns = listOf(
                    TableColumn(id = id),
                    TableColumn(id = id),
                ),
                rowKey = { it },
            )
        }
    }
}
