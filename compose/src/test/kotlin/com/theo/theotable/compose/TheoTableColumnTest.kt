package com.theo.theotable.compose

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theo.theotable.core.SortDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TheoTableColumnTest {
    private data class Dessert(
        val name: String,
        val calories: Int,
    )

    @Test
    fun theoTextColumn_usesContentWidthByDefaultAndCreatesTextHints() {
        val column = theoTextColumn<Dessert>(
            id = "name",
            title = "Name",
            value = { it.name },
        )

        val headerHint = column.headerWidthHint as TheoTableWidthHint.Text
        val cellHint = column.widthHint?.invoke(Dessert("Cupcake", 305)) as TheoTableWidthHint.Text

        assertTrue(column.width is TheoTableColumnWidth.Content)
        assertEquals("Name", headerHint.value)
        assertEquals("Cupcake", cellHint.value)
        assertNull(headerHint.style)
        assertNull(cellHint.style)
    }

    @Test
    fun theoTextColumn_preservesColumnOverrides() {
        val headerStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        val cellStyle = TextStyle(fontSize = 12.sp)
        val headerBackground = Color(0xFFE8F1FF)
        val cellBackground = Color(0xFFF4F8FF)

        val column = theoTextColumn<Dessert>(
            id = "calories",
            title = "Calories",
            value = { it.calories.toString() },
            width = TheoTableColumnWidth.Fixed(120.dp),
            defaultSortDirection = SortDirection.Descending,
            headerAlignment = Alignment.Center,
            cellAlignment = Alignment.CenterEnd,
            headerTextStyle = headerStyle,
            cellTextStyle = cellStyle,
            headerBackground = headerBackground,
            cellBackground = cellBackground,
        )

        val headerHint = column.headerWidthHint as TheoTableWidthHint.Text
        val cellHint = column.widthHint?.invoke(Dessert("Cupcake", 305)) as TheoTableWidthHint.Text

        assertEquals(TheoTableColumnWidth.Fixed(120.dp), column.width)
        assertEquals(SortDirection.Descending, column.defaultSortDirection)
        assertEquals(Alignment.Center, column.headerAlignment)
        assertEquals(Alignment.CenterEnd, column.cellAlignment)
        assertEquals(headerBackground, column.headerBackground)
        assertEquals(cellBackground, column.cellBackground)
        assertEquals(headerStyle, headerHint.style)
        assertEquals(cellStyle, cellHint.style)
        assertEquals("305", cellHint.value)
    }

    @Test
    fun theoTextColumn_preservesTextLayoutHints() {
        val column = theoTextColumn<Dessert>(
            id = "name",
            title = "Dessert name",
            value = { it.name },
            maxLines = 2,
            overflow = TextOverflow.Clip,
        )

        val headerHint = column.headerWidthHint as TheoTableWidthHint.Text
        val cellHint = column.widthHint?.invoke(Dessert("Chocolate cake", 420)) as TheoTableWidthHint.Text

        assertEquals(2, headerHint.maxLines)
        assertEquals(2, cellHint.maxLines)
        assertEquals(TextOverflow.Clip, headerHint.overflow)
        assertEquals(TextOverflow.Clip, cellHint.overflow)
        assertTrue(headerHint.softWrap)
        assertTrue(cellHint.softWrap)
    }

    @Test
    fun asCoreColumn_usesComparatorOnlyWhenColumnIsSortable() {
        val comparator = compareBy<Dessert> { it.calories }
        val sortableColumn = theoTextColumn(
            id = "sortable",
            title = "Sortable",
            value = { it.calories.toString() },
            comparator = comparator,
        )
        val nonSortableColumn = theoTextColumn(
            id = "not-sortable",
            title = "Not sortable",
            value = { it.calories.toString() },
            comparator = comparator,
            sortable = false,
        )

        assertNotNull(sortableColumn.asCoreColumn().comparator)
        assertNull(nonSortableColumn.asCoreColumn().comparator)
    }
}
