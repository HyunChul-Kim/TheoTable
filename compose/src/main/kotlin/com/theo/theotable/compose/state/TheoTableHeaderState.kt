package com.theo.theotable.compose.state

import androidx.compose.runtime.Immutable
import com.theo.theotable.core.SortDirection

@Immutable
data class TheoTableHeaderState(
    val isSorted: Boolean,
    val sortDirection: SortDirection?,
    val sortable: Boolean,
    val sortingEnabled: Boolean,
)
