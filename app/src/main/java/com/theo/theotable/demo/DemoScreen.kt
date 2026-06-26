package com.theo.theotable.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.theo.theotable.compose.TheoTable
import com.theo.theotable.compose.column.TheoTableColumnWidthResolvingMode
import com.theo.theotable.compose.column.theoTextColumn
import com.theo.theotable.compose.state.rememberTheoTableState
import com.theo.theotable.compose.style.TheoTableBackgroundStyle
import com.theo.theotable.compose.style.TheoTableColumnBackground
import com.theo.theotable.compose.style.TheoTableStyle
import com.theo.theotable.compose.style.TheoTableTextStyle
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.demo.component.ColumnOptionControls
import com.theo.theotable.demo.component.TableOptionControls
import com.theo.theotable.demo.model.DemoUiState
import com.theo.theotable.demo.model.option.DemoColumnOptions
import com.theo.theotable.demo.model.option.DemoColumnStructureOption
import com.theo.theotable.demo.model.option.DemoDividerOptions
import com.theo.theotable.demo.model.option.toDividerColors
import com.theo.theotable.demo.model.option.toStructureOptions
import com.theo.theotable.demo.model.option.toWidth
import com.theo.theotable.demo.model.table.DemoDataSet
import com.theo.theotable.demo.model.table.DemoTableRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DemoScreen(
    viewModel: DemoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSettings by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TheoTable",
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSettings = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "settings",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        DemoContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            settingsSheetState = settingsSheetState,
            showSettings = showSettings,
            onShowSettingsChange = { isShow ->
                showSettings = isShow
            },
            onDataSetChange = viewModel::selectDataSet,
            onSortEnableChange = viewModel::updateSortingEnabled,
            onSelectionModeChange = viewModel::updateSelectionMode,
            onHeaderTextSizeChange = viewModel::updateHeaderTextSize,
            onCellTextSizeChange = viewModel::updateCellTextSize,
            onTableHeaderBackgroundChange = viewModel::updateTableHeaderBackgroundEnable,
            onTableCellBackgroundChange = viewModel::updateTableCellBackgroundEnable,
            onFrozenColumnCountChange = viewModel::updateFrozenColumnCount,
            onContentPaddingDpChange = viewModel::updateContentPadding,
            onDividerOptionsChange = viewModel::updateDividerOptions,
            onColumnOptionsChange = viewModel::updateColumnOptions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoContent(
    modifier: Modifier = Modifier,
    uiState: DemoUiState,
    settingsSheetState: SheetState,
    showSettings: Boolean,
    onShowSettingsChange: (Boolean) -> Unit,
    onDataSetChange: (DemoDataSet) -> Unit,
    onSortEnableChange: (Boolean) -> Unit,
    onSelectionModeChange: (SelectionMode) -> Unit,
    onHeaderTextSizeChange: (Float) -> Unit,
    onCellTextSizeChange: (Float) -> Unit,
    onTableHeaderBackgroundChange: (Boolean) -> Unit,
    onTableCellBackgroundChange: (Boolean) -> Unit,
    onFrozenColumnCountChange: (Int) -> Unit,
    onContentPaddingDpChange: (Float) -> Unit,
    onDividerOptionsChange: (DemoDividerOptions) -> Unit,
    onColumnOptionsChange: (String, DemoColumnOptions) -> Unit,
) {
    val columnStructureOptions = remember(uiState.columnOptions) {
        uiState.columnOptions.mapValues { (_, options) ->
            options.toStructureOptions()
        }
    }

    val columns = remember(uiState.tableData.headers, columnStructureOptions) {
        uiState.tableData.headers.map { header ->
            val options = columnStructureOptions[header.key] ?: DemoColumnStructureOption()

            theoTextColumn(
                id = header.key,
                title = header.name,
                value = { row: DemoTableRow -> row.values[header.key].orEmpty() },
                comparator = compareBy { row: DemoTableRow -> row.values[header.key].orEmpty() },
                width = options.toWidth(),
                sortable = options.sortable,
                maxLines = options.maxLines,
                cellAlignment = header.style.cellAlignment,
            )
        }
    }

    val columnBackgrounds = remember(uiState.tableData.headers, uiState.columnOptions) {
        uiState.tableData.headers.associate { header ->
            val options = uiState.columnOptions[header.key] ?: DemoColumnOptions()

            header.key to TheoTableColumnBackground(
                header = header.style.headerBackground.takeIf { options.headerTint },
                cell = header.style.cellBackground.takeIf { options.cellTint },
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            uiState.dataSets.forEach { dataSet ->
                OutlinedButton(
                    onClick = { onDataSetChange(dataSet) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if(uiState.selectedDataSet == dataSet) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.White
                        },
                        contentColor = if(uiState.selectedDataSet == dataSet) {
                            Color.White
                        } else {
                            Color.Gray
                        }
                    ),
                ) {
                    Text(
                        text = dataSet.label,
                        fontWeight = if(uiState.selectedDataSet == dataSet) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isDataLoading -> {
                    CircularProgressIndicator()
                }

                uiState.dataLoadError != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Failed to load data", fontWeight = FontWeight.Bold)
                        Text(uiState.dataLoadError)
                        OutlinedButton(onClick = { onDataSetChange(uiState.selectedDataSet) }) {
                            Text("Retry")
                        }
                    }
                }

                uiState.tableData.rows.isEmpty() || columns.isEmpty() -> {
                    Text("No data")
                }

                else -> {
                    key(uiState.selectedDataSet) {
                        val tableState = rememberTheoTableState<Int>()

                        TheoTable(
                            modifier = Modifier
                                .wrapContentWidth()
                                .fillMaxHeight()
                                .align(Alignment.TopStart),
                            rows = uiState.tableData.rows,
                            columns = columns,
                            rowKey = { it.id },
                            state = tableState,
                            style = TheoTableStyle(
                                text = TheoTableTextStyle(
                                    header = TextStyle(
                                        fontSize = uiState.headerTextSize.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF343434),
                                    ),
                                    cell = TextStyle(
                                        fontSize = uiState.cellTextSize.sp,
                                        color = Color(0xFF1F2937),
                                    ),
                                ),
                                background = TheoTableBackgroundStyle(
                                    header = if (uiState.tableHeaderBackground) Color(0xFFF5F5F5) else Color.Transparent,
                                    cell = if (uiState.tableCellBackground) Color.White else Color.Transparent,
                                    columns = columnBackgrounds,
                                ),
                                divider = uiState.dividerOptions.toDividerColors(),
                            ),
                            selectionMode = uiState.selectionMode,
                            sortingEnabled = uiState.sortingEnabled,
                            frozenColumnCount = uiState.frozenColumnCount,
                            columnWidthResolvingMode = TheoTableColumnWidthResolvingMode.Deferred(
                                fallbackWidth = 120.dp,
                                rowsPerChunk = 50,
                                minimumLoadingDurationMillis = 0L,
                                keepPreviousWidths = true,
                            ),
                            columnWidthLoadingContent = {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.White.copy(alpha = 0.65f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = uiState.contentPadding.dp),
                        )
                    }
                }
            }
        }
    }

    if(showSettings) {
        ModalBottomSheet(
            onDismissRequest = { onShowSettingsChange(false) },
            sheetState = settingsSheetState,
            scrimColor = Color.Black.copy(alpha = 0.12f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Settings", fontWeight = FontWeight.Bold)

                    OutlinedButton(onClick = { onShowSettingsChange(false) }) {
                        Text("Close")
                    }
                }

                TableOptionControls(
                    sortingEnabled = uiState.sortingEnabled,
                    onSortingEnabledChange = onSortEnableChange,
                    selectionMode = uiState.selectionMode,
                    onSelectionModeChange = onSelectionModeChange,
                    headerTextSize = uiState.headerTextSize,
                    onHeaderTextSizeChange = onHeaderTextSizeChange,
                    cellTextSize = uiState.cellTextSize,
                    onCellTextSizeChange = onCellTextSizeChange,
                    tableHeaderBackground = uiState.tableHeaderBackground,
                    onTableHeaderBackgroundChange = onTableHeaderBackgroundChange,
                    tableCellBackground = uiState.tableCellBackground,
                    onTableCellBackgroundChange = onTableCellBackgroundChange,
                    frozenColumnCount = uiState.frozenColumnCount,
                    maxFrozenColumnCount = columns.size,
                    onFrozenColumnCountChange = onFrozenColumnCountChange,
                    contentPaddingDp = uiState.contentPadding,
                    onContentPaddingDpChange = onContentPaddingDpChange,
                    dividerOptions = uiState.dividerOptions,
                    onDividerOptionsChange = onDividerOptionsChange,
                )

                HorizontalDivider()

                uiState.tableData.headers.forEach { header ->
                    ColumnOptionControls(
                        title = header.name,
                        options = uiState.columnOptions[header.key] ?: DemoColumnOptions(),
                        onChange = { onColumnOptionsChange(header.key, it) },
                    )
                }

                Spacer(modifier = Modifier.size(1.dp))
            }
        }
    }
}
