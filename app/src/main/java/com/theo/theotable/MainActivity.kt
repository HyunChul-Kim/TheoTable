package com.theo.theotable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theo.theotable.compose.TheoTable
import com.theo.theotable.compose.rememberTheoTableState
import com.theo.theotable.compose.theoTextColumn
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.model.DemoColumnOptions
import com.theo.theotable.model.DemoWidthMode
import com.theo.theotable.model.toWidth
import com.theo.theotable.ui.theme.TheoTableTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheoTableTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TableSample(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Dessert(
    val id: Int,
    val name: String,
    val calories: Int,
    val price: Int,
)

private val sampleDesserts = listOf(
    Dessert(1, "Cupcake", 305, 3500),
    Dessert(2, "Donut", 452, 2800),
    Dessert(3, "Eclair", 262, 4200),
    Dessert(4, "Froyo", 159, 3900),
    Dessert(5, "Gingerbread", 356, 3200),
)

@Composable
fun TableSample(modifier: Modifier = Modifier) {
    val tableState = rememberTheoTableState<Int>()

    var sortingEnabled by remember { mutableStateOf(true) }
    var selectionMode by remember { mutableStateOf(SelectionMode.None) }
    var headerTextSize by remember { mutableStateOf(14f) }
    var cellTextSize by remember { mutableStateOf(12f) }
    var tableHeaderBackground by remember { mutableStateOf(true) }
    var tableCellBackground by remember { mutableStateOf(true) }
    
    var nameOptions by remember { mutableStateOf(DemoColumnOptions()) }
    var caloriesOptions by remember { mutableStateOf(DemoColumnOptions()) }
    var priceOptions by remember { mutableStateOf(DemoColumnOptions()) }

    val columns = remember(nameOptions, caloriesOptions, priceOptions) {
        listOf(
            theoTextColumn(
                id = "name",
                title = "Name",
                value = { it.name },
                width = nameOptions.toWidth(),
                sortable = nameOptions.sortable,
                comparator = compareBy<Dessert> { it.name },
                headerBackground = if(nameOptions.headerTint) Color(0xFFFFF3D8) else null,
                cellBackground = if(nameOptions.cellTint) Color(0xFFFFFBF0) else null,
            ),
            theoTextColumn(
                id = "calories",
                title = "Calories",
                value = { it.calories.toString() },
                width = caloriesOptions.toWidth(),
                sortable = caloriesOptions.sortable,
                comparator = compareBy<Dessert> { it.calories },
                cellAlignment = Alignment.CenterEnd,
                headerBackground = if (caloriesOptions.headerTint) Color(0xFFE8F1FF) else null,
                cellBackground = if (caloriesOptions.cellTint) Color(0xFFF4F8FF) else null,
            ),
            theoTextColumn(
                id = "price",
                title = "Price",
                value = { "${it.price}원" },
                width = priceOptions.toWidth(),
                sortable = priceOptions.sortable,
                comparator = compareBy<Dessert> { it.price },
                cellAlignment = Alignment.CenterEnd,
                headerBackground = if (priceOptions.headerTint) Color(0xFFEAF7EA) else null,
                cellBackground = if (priceOptions.cellTint) Color(0xFFF4FBF4) else null,
            ),
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TheoTable(
            rows = sampleDesserts,
            columns = columns,
            rowKey = { it.id },
            state = tableState,
            selectionMode = selectionMode,
            sortingEnabled = sortingEnabled,
            headerTextStyle = TextStyle(
                fontSize = headerTextSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF343434),
            ),
            cellTextStyle = TextStyle(
                fontSize = cellTextSize.sp,
                color = Color(0xFF1F2937),
            ),
            headerBackground = if (tableHeaderBackground) Color(0xFFF5F5F5) else Color.Transparent,
            cellBackground = if (tableCellBackground) Color.White else Color.Transparent,
            modifier = Modifier.height(260.dp),
        )

        HorizontalDivider()

        TableOptionControls(
            sortingEnabled = sortingEnabled,
            onSortingEnabledChange = { sortingEnabled = it },
            selectionMode = selectionMode,
            onSelectionModeChange = { selectionMode = it },
            headerTextSize = headerTextSize,
            onHeaderTextSizeChange = { headerTextSize = it },
            cellTextSize = cellTextSize,
            onCellTextSizeChange = { cellTextSize = it },
            tableHeaderBackground = tableHeaderBackground,
            onTableHeaderBackgroundChange = { tableHeaderBackground = it },
            tableCellBackground = tableCellBackground,
            onTableCellBackgroundChange = { tableCellBackground = it },
        )

        HorizontalDivider()

        ColumnOptionControls("Name", nameOptions) { nameOptions = it }
        ColumnOptionControls("Calories", caloriesOptions) { caloriesOptions = it }
        ColumnOptionControls("Price", priceOptions) { priceOptions = it }
    }
}

@Composable
private fun TableOptionControls(
    sortingEnabled: Boolean,
    onSortingEnabledChange: (Boolean) -> Unit,
    selectionMode: SelectionMode,
    onSelectionModeChange: (SelectionMode) -> Unit,
    headerTextSize: Float,
    onHeaderTextSizeChange: (Float) -> Unit,
    cellTextSize: Float,
    onCellTextSizeChange: (Float) -> Unit,
    tableHeaderBackground: Boolean,
    onTableHeaderBackgroundChange: (Boolean) -> Unit,
    tableCellBackground: Boolean,
    onTableCellBackgroundChange: (Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Table options", fontWeight = FontWeight.Bold)

        OptionSwitch("Sorting enabled", sortingEnabled, onSortingEnabledChange)

        Text("Selection mode")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SelectionModeChip("None", selectionMode, SelectionMode.None, onSelectionModeChange)
            SelectionModeChip("Single", selectionMode, SelectionMode.Single, onSelectionModeChange)
            SelectionModeChip("Multiple", selectionMode, SelectionMode.Multiple, onSelectionModeChange)
        }

        Text("Header text: ${headerTextSize.toInt()}sp")
        Slider(
            value = headerTextSize,
            onValueChange = onHeaderTextSizeChange,
            valueRange = 10f..22f,
        )

        Text("Cell text: ${cellTextSize.toInt()}sp")
        Slider(
            value = cellTextSize,
            onValueChange = onCellTextSizeChange,
            valueRange = 10f..22f,
        )

        OptionSwitch("Header background", tableHeaderBackground, onTableHeaderBackgroundChange)
        OptionSwitch("Cell background", tableCellBackground, onTableCellBackgroundChange)
    }
}

@Composable
private fun ColumnOptionControls(
    title: String,
    options: DemoColumnOptions,
    onChange: (DemoColumnOptions) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$title column", fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = options.widthMode == DemoWidthMode.Content,
                onClick = { onChange(options.copy(widthMode = DemoWidthMode.Content)) },
                label = { Text("Auto width") },
            )
            FilterChip(
                selected = options.widthMode == DemoWidthMode.Fixed,
                onClick = { onChange(options.copy(widthMode = DemoWidthMode.Fixed)) },
                label = { Text("Fixed width") },
            )
        }

        Text("Fixed width: ${options.fixedWidthDp.toInt()}dp")
        Slider(
            value = options.fixedWidthDp,
            onValueChange = { onChange(options.copy(fixedWidthDp = it)) },
            valueRange = 80f..260f,
            enabled = options.widthMode == DemoWidthMode.Fixed,
        )

        OptionSwitch("Sortable", options.sortable) {
            onChange(options.copy(sortable = it))
        }
        OptionSwitch("Header tint", options.headerTint) {
            onChange(options.copy(headerTint = it))
        }
        OptionSwitch("Cell tint", options.cellTint) {
            onChange(options.copy(cellTint = it))
        }
    }
}

@Composable
private fun OptionSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(text)
    }
}

@Composable
private fun SelectionModeChip(
    text: String,
    current: SelectionMode,
    mode: SelectionMode,
    onChange: (SelectionMode) -> Unit,
) {
    FilterChip(
        selected = current == mode,
        onClick = { onChange(mode) },
        label = { Text(text) },
    )
}

@Preview
@Composable
private fun TableSamplePreview() {
    TheoTableTheme {
        TableSample()
    }
}