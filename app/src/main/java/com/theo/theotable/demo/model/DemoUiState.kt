package com.theo.theotable.demo.model

import com.theo.theotable.core.SelectionMode
import com.theo.theotable.demo.model.option.DemoColumnOptions
import com.theo.theotable.demo.model.option.DemoDividerOptions
import com.theo.theotable.demo.model.table.DemoDataSet
import com.theo.theotable.demo.model.table.DemoTableData

data class DemoUiState(
    val dataSets: List<DemoDataSet> = DemoDataSet.entries,
    val selectedDataSet: DemoDataSet = DemoDataSet.Simple,
    val tableData: DemoTableData = DemoTableData.Empty,
    val isDataLoading: Boolean = false,
    val dataLoadError: String? = null,

    val sortingEnabled: Boolean = true,
    val selectionMode: SelectionMode = SelectionMode.None,
    val headerTextSize: Float = 14f,
    val cellTextSize: Float = 12f,
    val tableHeaderBackground: Boolean = true,
    val tableCellBackground: Boolean = true,
    val contentPadding: Float = 0f,
    val columnOptions: Map<String, DemoColumnOptions> = emptyMap(),
    val dividerOptions: DemoDividerOptions = DemoDividerOptions(),
    val frozenColumnCount: Int = 0,
)