package com.theo.theotable.core

data class TableState<K>(
    val sort: TableSort = TableSort(),
    val selection: TableSelection<K> = TableSelection(),
)