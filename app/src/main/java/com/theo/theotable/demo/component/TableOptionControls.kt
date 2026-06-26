package com.theo.theotable.demo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theo.theotable.core.SelectionMode
import com.theo.theotable.demo.model.option.DemoDividerOptions
import kotlin.math.roundToInt

@Composable
internal fun TableOptionControls(
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