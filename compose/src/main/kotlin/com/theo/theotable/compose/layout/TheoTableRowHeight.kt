package com.theo.theotable.compose.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theo.theotable.compose.style.TheoTableDefaults

sealed interface TheoTableRowHeight {
    data class Fixed(val value: Dp): TheoTableRowHeight {
        init {
            require(value > 0.dp) { "Fixed value must be greater than zero." }
        }
    }

    data class WrapContent(val min: Dp = TheoTableDefaults.RowMinHeight): TheoTableRowHeight {
        init {
            require(min > 0.dp) { "Min must be greater than zero." }
        }
    }
}