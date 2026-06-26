package com.theo.theotable.demo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theo.theotable.demo.model.option.DemoColumnOptions
import com.theo.theotable.demo.model.option.DemoWidthMode
import kotlin.math.roundToInt

@Composable
internal fun ColumnOptionControls(
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
        Text("Max lines: ${options.maxLines}")
        Slider(
            value = options.maxLines.toFloat(),
            onValueChange = { onChange(options.copy(maxLines = it.roundToInt().coerceIn(1, 5))) },
            valueRange = 1f..5f,
            steps = 3,
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