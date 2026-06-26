package com.theo.theotable.demo.model.table

data class DemoTableHeader(
    val key: String,
    val name: String,
    val style: DemoTableColumnStyle = DemoTableColumnStyle(),
)