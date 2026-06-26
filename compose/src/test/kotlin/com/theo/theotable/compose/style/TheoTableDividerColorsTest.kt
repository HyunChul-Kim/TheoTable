package com.theo.theotable.compose.style

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class TheoTableDividerColorsTest {
    @Test
    fun none_makesEveryDividerTransparent() {
        val colors = TheoTableDividerColors.None

        assertEquals(Color.Transparent, colors.border)
        assertEquals(Color.Transparent, colors.headerVertical)
        assertEquals(Color.Transparent, colors.headerHorizontal)
        assertEquals(Color.Transparent, colors.cellVertical)
        assertEquals(Color.Transparent, colors.cellHorizontal)
    }

    @Test
    fun all_appliesColorToEveryDivider() {
        val color = Color.Red
        val colors = TheoTableDividerColors.all(color)

        assertEquals(color, colors.border)
        assertEquals(color, colors.headerVertical)
        assertEquals(color, colors.headerHorizontal)
        assertEquals(color, colors.cellVertical)
        assertEquals(color, colors.cellHorizontal)
    }

    @Test
    fun inner_keepsBorderTransparent() {
        val color = Color.Blue
        val colors = TheoTableDividerColors.inner(color)

        assertEquals(Color.Transparent, colors.border)
        assertEquals(color, colors.headerVertical)
        assertEquals(color, colors.headerHorizontal)
        assertEquals(color, colors.cellVertical)
        assertEquals(color, colors.cellHorizontal)
    }

    @Test
    fun onlyHeaderVertical_appliesColorOnlyToHeaderVerticalDivider() {
        val color = Color.Green
        val colors = TheoTableDividerColors.onlyHeaderVertical(color)

        assertEquals(Color.Transparent, colors.border)
        assertEquals(color, colors.headerVertical)
        assertEquals(Color.Transparent, colors.headerHorizontal)
        assertEquals(Color.Transparent, colors.cellVertical)
        assertEquals(Color.Transparent, colors.cellHorizontal)
    }

    @Test
    fun onlyCellHorizontal_appliesColorOnlyToCellHorizontalDivider() {
        val color = Color.Yellow
        val colors = TheoTableDividerColors.onlyCellHorizontal(color)

        assertEquals(Color.Transparent, colors.border)
        assertEquals(Color.Transparent, colors.headerVertical)
        assertEquals(Color.Transparent, colors.headerHorizontal)
        assertEquals(Color.Transparent, colors.cellVertical)
        assertEquals(color, colors.cellHorizontal)
    }
}