package com.theo.theotable.demo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theo.theotable.demo.model.option.DemoDividerOptions

@Composable
internal fun DividerOptionControls(
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