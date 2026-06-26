package com.theo.theotable.demo.model.table

data class DemoTableData(
    val headers: List<DemoTableHeader>,
    val rows: List<DemoTableRow>,
) {
    companion object {
        val Empty = DemoTableData(headers = emptyList(), rows = emptyList())
    }
}
