package com.theo.theotable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theo.theotable.compose.TheoTable
import com.theo.theotable.compose.TheoTableBackgroundStyle
import com.theo.theotable.compose.TheoTableColumn
import com.theo.theotable.compose.TheoTableDividerColors
import com.theo.theotable.compose.TheoTableStyle
import com.theo.theotable.compose.TheoTableTextStyle
import com.theo.theotable.compose.rememberTheoTableState
import com.theo.theotable.compose.theoTextColumn
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.model.DemoColumnOptions
import com.theo.theotable.model.DemoWidthMode
import com.theo.theotable.model.maxLinesForWidthMode
import com.theo.theotable.model.toWidth
import com.theo.theotable.ui.theme.TheoTableTheme
import kotlin.math.roundToInt

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
    val fat: Double,
    val carbs: Int,
    val protein: Double,
    val sodium: Int,
    val price: Int,
)

private val sampleDesserts = listOf(
    Dessert(1, "Cupcake", 305, 3.7, 67, 4.3, 413, 3500),
    Dessert(2, "Donut", 452, 25.0, 51, 4.9, 326, 2800),
    Dessert(3, "Eclair", 262, 16.0, 24, 6.0, 337, 4200),
    Dessert(4, "Frozen yogurt with honey topping", 159, 6.0, 24, 4.0, 87, 3900),
    Dessert(5, "Gingerbread", 356, 16.0, 49, 3.9, 327, 3200),
    Dessert(6, "Honeycomb", 408, 3.2, 87, 6.5, 562, 4500),
    Dessert(7, "Ice cream sandwich", 237, 9.0, 37, 4.3, 129, 3700),
    Dessert(8, "Jelly bean assortment pack", 375, 0.0, 94, 0.0, 50, 2500),
    Dessert(9, "KitKat", 518, 26.0, 65, 7.0, 54, 3300),
    Dessert(10, "Lollipop", 392, 0.2, 98, 0.0, 38, 1900),
    Dessert(11, "Marshmallow", 318, 0.2, 81, 1.8, 80, 2200),
    Dessert(12, "Nougat bar with roasted nuts", 360, 19.0, 43, 3.0, 210, 4100),
    Dessert(13, "Oreo", 437, 18.0, 63, 4.0, 203, 3000),
)

private data class DemoDividerOptions(
    val border: Boolean = true,
    val headerVertical: Boolean = true,
    val headerHorizontal: Boolean = true,
    val cellVertical: Boolean = true,
    val cellHorizontal: Boolean = true,
    val accentColors: Boolean = true,
)

private fun DemoDividerOptions.toDividerColors(): TheoTableDividerColors {
    val defaultColor = Color(0xFFE0E0E0)

    val borderColor = if(accentColors) Color(0xFF6B7280) else defaultColor
    val headerVerticalColor = if(accentColors) Color(0xFF2563EB) else defaultColor
    val headerHorizontalColor = if(accentColors) Color(0xFF9333EA) else defaultColor
    val cellVerticalColor = if(accentColors) Color(0xFF16A34A) else defaultColor
    val cellHorizontalColor = if(accentColors) Color(0xFFEA580C) else defaultColor

    return TheoTableDividerColors(
        border = if(border) borderColor else Color.Transparent,
        headerVertical = if(headerVertical) headerVerticalColor else Color.Transparent,
        headerHorizontal = if(headerHorizontal) headerHorizontalColor else Color.Transparent,
        cellVertical = if(cellVertical) cellVerticalColor else Color.Transparent,
        cellHorizontal = if(cellHorizontal) cellHorizontalColor else Color.Transparent,
    )
}

private data class DessertColumnSpec(
    val id: String,
    val title: String,
    val value: (Dessert) -> String,
    val comparator: Comparator<Dessert>,
    val cellAlignment: Alignment = Alignment.CenterStart,
    val headerTint: Color,
    val cellTint: Color,
)

private val dessertColumnSpecs = listOf(
    DessertColumnSpec(
        id = "name",
        title = "Name",
        value = { it.name },
        comparator = compareBy { it.name },
        headerTint = Color(0xFFFFF3D8),
        cellTint = Color(0xFFFFFBF0),
    ),
    DessertColumnSpec(
        id = "calories",
        title = "Calories",
        value = { it.calories.toString() },
        comparator = compareBy { it.calories },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFE8F1FF),
        cellTint = Color(0xFFF4F8FF),
    ),
    DessertColumnSpec(
        id = "fat",
        title = "Fat",
        value = { "${it.fat}g" },
        comparator = compareBy { it.fat },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFFFEFEF),
        cellTint = Color(0xFFFFF7F7),
    ),
    DessertColumnSpec(
        id = "carbs",
        title = "Carbs",
        value = { "${it.carbs}g" },
        comparator = compareBy { it.carbs },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFEFF6FF),
        cellTint = Color(0xFFF8FBFF),
    ),
    DessertColumnSpec(
        id = "protein",
        title = "Protein",
        value = { "${it.protein}g" },
        comparator = compareBy { it.protein },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFF0FDF4),
        cellTint = Color(0xFFF7FEF9),
    ),
    DessertColumnSpec(
        id = "sodium",
        title = "Sodium",
        value = { "${it.sodium}mg" },
        comparator = compareBy { it.sodium },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFFFF7ED),
        cellTint = Color(0xFFFFFBF5),
    ),
    DessertColumnSpec(
        id = "price",
        title = "Price",
        value = { "${it.price}KRW" },
        comparator = compareBy { it.price },
        cellAlignment = Alignment.CenterEnd,
        headerTint = Color(0xFFEAF7EA),
        cellTint = Color(0xFFF4FBF4),
    ),
)

private fun DessertColumnSpec.toColumn(
    options: DemoColumnOptions,
): TheoTableColumn<Dessert> {
    return theoTextColumn(
        id = id,
        title = title,
        value = value,
        width = options.toWidth(),
        sortable = options.sortable,
        comparator = comparator,
        cellAlignment = cellAlignment,
        headerBackground = if (options.headerTint) headerTint else null,
        cellBackground = if (options.cellTint) cellTint else null,
        maxLines = options.maxLinesForWidthMode()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableSample(modifier: Modifier = Modifier) {
    val tableState = rememberTheoTableState<Int>()

    var sortingEnabled by remember { mutableStateOf(true) }
    var selectionMode by remember { mutableStateOf(SelectionMode.None) }
    var headerTextSize by remember { mutableStateOf(14f) }
    var cellTextSize by remember { mutableStateOf(12f) }
    var tableHeaderBackground by remember { mutableStateOf(true) }
    var tableCellBackground by remember { mutableStateOf(true) }
    var contentPaddingDp by remember { mutableStateOf(0f) }

    val columnOptions = remember {
        dessertColumnSpecs
            .map { it.id to DemoColumnOptions() }
            .toMutableStateMap()
    }

    var dividerOptions by remember { mutableStateOf(DemoDividerOptions()) }

    var frozenColumnCount by remember { mutableStateOf(0) }

    val columns = remember(columnOptions.toMap()) {
        dessertColumnSpecs.map { spec ->
            spec.toColumn(columnOptions.getValue(spec.id))
        }
    }

    var showSettings by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("TheoTable", fontWeight = FontWeight.Bold)
                Text("Dessert nutrition sample")
            }

            Button(onClick = { showSettings = true }) {
                Text("Settings")
            }
        }
        TheoTable(
            rows = sampleDesserts,
            columns = columns,
            rowKey = { it.id },
            state = tableState,
            style = TheoTableStyle(
                text = TheoTableTextStyle(
                    header = TextStyle(
                        fontSize = headerTextSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF343434),
                    ),
                    cell = TextStyle(
                        fontSize = cellTextSize.sp,
                        color = Color(0xFF1F2937),
                    ),
                ),
                background = TheoTableBackgroundStyle(
                    header = if (tableHeaderBackground) Color(0xFFF5F5F5) else Color.Transparent,
                    cell = if (tableCellBackground) Color.White else Color.Transparent,
                ),
                divider = dividerOptions.toDividerColors(),
            ),
            selectionMode = selectionMode,
            sortingEnabled = sortingEnabled,
            frozenColumnCount = frozenColumnCount,
            contentPadding = PaddingValues(horizontal = contentPaddingDp.dp),
        )
    }

    if(showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
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

                    OutlinedButton(onClick = { showSettings = false }) {
                        Text("Close")
                    }
                }

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
                    frozenColumnCount = frozenColumnCount,
                    maxFrozenColumnCount = columns.size,
                    onFrozenColumnCountChange = { frozenColumnCount = it },
                    contentPaddingDp = contentPaddingDp,
                    onContentPaddingDpChange = { contentPaddingDp = it },
                    dividerOptions = dividerOptions,
                    onDividerOptionsChange = { dividerOptions = it },
                )

                HorizontalDivider()

                dessertColumnSpecs.forEach { spec ->
                    ColumnOptionControls(
                        title = spec.title,
                        options = columnOptions.getValue(spec.id),
                        onChange = { columnOptions[spec.id] = it },
                    )
                }

                Spacer(modifier = Modifier.size(1.dp))
            }
        }
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
    frozenColumnCount: Int,
    maxFrozenColumnCount: Int,
    onFrozenColumnCountChange: (Int) -> Unit,
    contentPaddingDp: Float,
    onContentPaddingDpChange: (Float) -> Unit,
    dividerOptions: DemoDividerOptions,
    onDividerOptionsChange: (DemoDividerOptions) -> Unit,
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

        Text("Frozen columns: $frozenColumnCount")
        Slider(
            value = frozenColumnCount.toFloat(),
            onValueChange = {
                onFrozenColumnCountChange(it.roundToInt().coerceIn(0, maxFrozenColumnCount))
            },
            valueRange = 0f..maxFrozenColumnCount.toFloat(),
            steps = (maxFrozenColumnCount - 1).coerceAtLeast(0),
        )

        Text("Content padding: ${contentPaddingDp.toInt()}dp")
        Slider(
            value = contentPaddingDp,
            onValueChange = onContentPaddingDpChange,
            valueRange = 0f..48f,
        )

        HorizontalDivider()

        DividerOptionControls(
            options = dividerOptions,
            onChange = onDividerOptionsChange,
        )


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
        Text("Max lines: ${options.maxLines} (Fixed width only)")
        Slider(
            value = options.maxLines.toFloat(),
            onValueChange = { onChange(options.copy(maxLines = it.roundToInt().coerceIn(1, 5))) },
            valueRange = 1f..5f,
            steps = 3,
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
private fun DividerOptionControls(
    options: DemoDividerOptions,
    onChange: (DemoDividerOptions) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Dividers", fontWeight = FontWeight.Bold)

        OptionSwitch("Table border", options.border) {
            onChange(options.copy(border = it))
        }
        OptionSwitch("Header vertical", options.headerVertical) {
            onChange(options.copy(headerVertical = it))
        }
        OptionSwitch("Header horizontal", options.headerHorizontal) {
            onChange(options.copy(headerHorizontal = it))
        }
        OptionSwitch("Cell vertical", options.cellVertical) {
            onChange(options.copy(cellVertical = it))
        }
        OptionSwitch("Cell horizontal", options.cellHorizontal) {
            onChange(options.copy(cellHorizontal = it))
        }
        OptionSwitch("Accent divider colors", options.accentColors) {
            onChange(options.copy(accentColors = it))
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
