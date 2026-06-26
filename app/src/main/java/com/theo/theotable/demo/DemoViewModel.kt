package com.theo.theotable.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.demo.model.DemoUiState
import com.theo.theotable.demo.model.option.DemoColumnOptions
import com.theo.theotable.demo.model.option.DemoDividerOptions
import com.theo.theotable.demo.model.table.DemoDataSet
import com.theo.theotable.demo.model.table.DemoTableData
import com.theo.theotable.demo.repository.DemoTableDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val repository: DemoTableDataRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        selectDataSet(DemoDataSet.Simple)
    }

    fun selectDataSet(dataSet: DemoDataSet) {
        val currentState = _uiState.value
        if(currentState.selectedDataSet == dataSet &&
            currentState.tableData.rows.isNotEmpty() &&
            !currentState.isDataLoading) {
            return
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedDataSet = dataSet,
                    tableData = DemoTableData.Empty,
                    columnOptions = emptyMap(),
                    frozenColumnCount = 0,
                    isDataLoading = true,
                    dataLoadError = null,
                )
            }

            repository.load(dataSet)
                .onSuccess { tableData ->
                    _uiState.update { state ->
                        if(state.selectedDataSet != dataSet) {
                            state
                        } else {
                            state.copy(
                                tableData = tableData,
                                columnOptions = tableData.headers.associate { header ->
                                    header.key to DemoColumnOptions()
                                },
                                frozenColumnCount = state.frozenColumnCount.coerceIn(0, tableData.headers.size),
                                isDataLoading = false,
                                dataLoadError = null,
                            )
                        }
                    }
                }.onFailure { throwable ->
                    _uiState.update { state ->
                        if(state.selectedDataSet != dataSet) {
                            state
                        } else {
                            state.copy(
                                isDataLoading = false,
                                dataLoadError = throwable.message ?: throwable::class.java.simpleName,
                            )
                        }
                    }
                }
        }
    }

    fun updateSortingEnabled(value: Boolean) = _uiState.update { it.copy(sortingEnabled = value) }
    fun updateSelectionMode(value: SelectionMode) = _uiState.update { it.copy(selectionMode = value) }
    fun updateHeaderTextSize(size: Float) = _uiState.update { it.copy(headerTextSize = size) }
    fun updateCellTextSize(size: Float) = _uiState.update { it.copy(cellTextSize = size) }
    fun updateTableHeaderBackgroundEnable(value: Boolean) = _uiState.update { it.copy(tableHeaderBackground = value) }
    fun updateTableCellBackgroundEnable(value: Boolean) = _uiState.update { it.copy(tableCellBackground = value) }
    fun updateFrozenColumnCount(value: Int) = _uiState.update {
        it.copy(frozenColumnCount = value.coerceIn(0, it.tableData.headers.size))
    }
    fun updateContentPadding(value: Float) = _uiState.update { it.copy(contentPadding = value) }
    fun updateDividerOptions(options: DemoDividerOptions) = _uiState.update {
        it.copy(dividerOptions = options)
    }
    fun updateColumnOptions(key: String, options: DemoColumnOptions) {
        _uiState.update {
            it.copy(columnOptions = it.columnOptions + (key to options))
        }
    }
}