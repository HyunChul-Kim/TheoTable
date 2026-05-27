package com.theo.theotable.core

@JvmInline
value class TableColumnId(val value: String) {
    init {
        require(value.isNotBlank()) { "TableColumnId must not be blank." }
    }
}

data class TableColumn<T>(
    val id: TableColumnId,
    val comparator: Comparator<T>? = null,
    val defaultSortDirection: SortDirection = SortDirection.Ascending,
)
