package com.theo.theotable.core

enum class SortDirection {
    Ascending,
    Descending;

    fun reversed(): SortDirection = when(this) {
        Ascending -> Descending
        Descending -> Ascending
    }
}

data class SortSpec(
    val columnId: TableColumnId,
    val direction: SortDirection,
)

data class TableSort(
    val specs: List<SortSpec> = emptyList(),
) {
    fun toggle(column: TableColumn<*>, multiSort: Boolean = false): TableSort {
        val current = specs.firstOrNull { it.columnId == column.id }

        val nextSpec = when {
            current == null -> SortSpec(column.id, column.defaultSortDirection)
            current.direction == column.defaultSortDirection -> current.copy(
                direction = current.direction.reversed()
            )
            else -> null
        }

        val remaining = if(multiSort) {
            specs.filterNot { it.columnId == column.id }
        } else {
            emptyList()
        }

        return copy(specs = if(nextSpec == null) remaining else remaining + nextSpec)
    }
}