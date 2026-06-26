package com.theo.theotable.demo.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.theo.theotable.core.SelectionMode

@Composable
internal fun SelectionModeChip(
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