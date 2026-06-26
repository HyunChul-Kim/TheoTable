package com.theo.theotable.core

class TableEngine<T, K>(
    private val columns: List<TableColumn<T>>,
    private val rowKey: (T) -> K,
) {
    init {
        val duplicated = columns
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .keys

        require(duplicated.isEmpty()) {
            "Duplicated table column ids: ${duplicated.joinToString { it.value }}"
        }
    }

    fun snapshot(
        rows: List<T>,
        state: TableState<K> = TableState(),
    ): TableSnapshot<T, K> {
        val sortedRows = sortedRows(rows, state.sort)
        val rowKeys = sortedRows.map(rowKey)

        return TableSnapshot(
            rows = sortedRows,
            rowKeys = rowKeys,
            columns = columns,
            state = state,
        )
    }

    fun sortedRows(
        rows: List<T>,
        sort: TableSort = TableSort(),
    ): List<T> {
        return rows.sortedWithSort(sort)
    }

    private fun List<T>.sortedWithSort(sort: TableSort): List<T> {
        if(sort.specs.isEmpty()) return this

        val comparator = sort.specs
            .mapNotNull { spec ->
                val columnComparator = columns
                    .firstOrNull { it.id == spec.columnId }
                    ?.comparator

                columnComparator?.withDirection(spec.direction)
            }
            .reduceOrNull { current, next ->
                current.then(next)
            }

        return if(comparator == null) this else sortedWith(comparator)
    }

    private fun Comparator<T>.withDirection(direction: SortDirection): Comparator<T> {
        return when(direction) {
            SortDirection.Ascending -> this
            SortDirection.Descending -> reversed()
        }
    }
}

data class TableSnapshot<T, K>(
    val rows: List<T>,
    val rowKeys: List<K>,
    val columns: List<TableColumn<T>>,
    val state: TableState<K>,
)