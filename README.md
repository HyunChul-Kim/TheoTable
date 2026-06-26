# TheoTable

TheoTable is a Jetpack Compose table library with a small Kotlin core module for table behavior and a Compose module for rendering table UI.

## Modules

- `core`: sorting and selection state logic.
- `compose`: Jetpack Compose table components.
- `app`: sample app for trying table options.

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.hyunchul-kim:theotable-compose:0.4.2")
}
```
Use theotable-core directly only when you need the non-UI table logic without Compose.
```kotlin
dependencies {
    implementation("io.github.hyunchul-kim:theotable-core:0.4.2")
}
```

## Requirements
- Android minSdk 21+
- Jetpack Compose
- Kotlin 2.0+

## Quick Start
TheoTable renders rows lazily, so place it in a bounded height container.
```kotlin
Column(Modifier.fillMaxSize()) {
    TheoTable(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        rows = rows,
        columns = columns,
        rowKey = { it.id },
        style = TheoTableDefaults.style(),
    )
}
```

## Columns
For simple text tables, use `theoTextColumn`.
```kotlin
val columns = listOf(
    theoTextColumn<User>(
        id = "name",
        title = "Name",
        value = { it.name },
        comparator = compareBy { it.name },
    ),
    theoTextColumn<User>(
        id = "email",
        title = "Email",
        value = { it.email },
        comparator = compareBy { it.email },
    ),
)
```
Use stable, unique, non-null row keys.
```kotlin
rowKey = { row -> row.id }
```

## Row Height
By default, TheoTable preserves wrap-content row behavior.
```kotlin
rowHeight = TheoTableRowHeight.WrapContent()
```
This is useful when cells may contain multi-line content.

For large datasets, prefer fixed row height to avoid intrinsic row measurement.
```kotlin
rowHeight = TheoTableRowHeight.Fixed(TheoTableDefaults.RowHeight)
```

## Column Width
Columns can use fixed width or content-based width.
```kotlin
theoTextColumn<User>(
    id = "name",
    title = "Name",
    value = { it.name },
    width = TheoTableColumnWidth.Fixed(160.dp),
)
```
For content-based width, TheoTable can measure sampled rows or all rows.
```kotlin
width = TheoTableColumnWidth.Content(
    strategy = TheoTableContentWidthStrategy.Sampled(count = 100),
)
```
`ExactAllRows` provides more accurate content width,
but it measures every row for that column. For large datasets,
prefer `Sampled` or `Fixed`.
```kotlin
width = TheoTableColumnWidth.Content(
    strategy = TheoTableContentWidthStrategy.ExactAllRows,
)
```

### Deferred Width Loading
For large content-based tables, use deferred width resolving to show fallback widths first and update the table when measured widths are ready.
`TheoTableState.columnWidthResolutionStatus`, `isColumnWidthResolving`, and `isColumnWidthResolved` let parent UI combine table width resolving with its own data loading state.
```kotlin
val tableState = rememberTheoTableState<Long>()
val hasTableContent = !isDataLoading && rows.isNotEmpty()
val showLoading = isDataLoading || (hasTableContent && !tableState.isColumnWidthResolved)

Box {
    if(!isDataLoading) {
        TheoTable(
            rows = rows,
            columns = columns,
            rowKey = { it.id },
            state = tableState,
            style = TheoTableDefaults.style(),
            columnWidthResolvingMode = TheoTableColumnWidthResolvingMode.Deferred(
                renderContentWhileResolving = false,
            ),
            columnWidthLoadingContent = null,
        )
    }

    if(showLoading) {
        TableLoadingOverlay()
    }
}
```

## Sorting And Selection
Sorting is enabled by default when a column has a comparator.
```kotlin
val tableState = rememberTheoTableState<Long>()

TheoTable(
    rows = rows,
    columns = columns,
    rowKey = { it.id },
    state = tableState,
    selectionMode = SelectionMode.Multiple,
    sortingEnabled = true,
    style = TheoTableDefaults.style(),
)
```

## Frozen Columns
Use `frozenColumnCount` to keep leading columns fixed while horizontally scrolling the remaining columns.
```kotlin
TheoTable(
    rows = rows,
    columns = columns,
    rowKey = { it.id },
    frozenColumnCount = 1,
    style = TheoTableDefaults.style(),
)
```

## Styling
You can use `TheoTableStyle` directly or start from `TheoTableDefaults`.
```kotlin
TheoTable(
    rows = rows,
    columns = columns,
    rowKey = { it.id },
    style = TheoTableStyle(
        text = TheoTableTextStyle(
            header = TextStyle(fontWeight = FontWeight.Bold),
            cell = TextStyle.Default,
        ),
        background = TheoTableBackgroundStyle(
            header = Color(0xFFF7F7F7),
            cell = Color.White,
            selectedRow = Color(0xFFE8F1FF),
        ),
        divider = TheoTableDividerColors.all(),
    ),
)
```

## Performance Notes
- TheoTable uses `LazyColumn` internally for vertical row rendering.
- The table must be placed in a bounded height container, such as `Modifier.weight(1f)` inside a full-height `Column`.
- Prefer `TheoTableRowHeight.Fixed(...)` for large datasets.
- Prefer `TheoTableContentWidthStrategy.Sampled(...)` or `TheoTableColumnWidth.Fixed(...)` for large datasets.
- Keep `rows` and `columns` stable where possible to avoid unnecessary width recalculation.

## License
TheoTable is released under the Apache License 2.0.
