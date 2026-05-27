package com.theo.theotable.compose

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertThrows
import org.junit.Test

class TheoTableColumnWidthTest {
    @Test
    fun fixed_rejectsNonPositiveWidth() {
        assertThrows(IllegalArgumentException::class.java) {
            TheoTableColumnWidth.Fixed(0.dp)
        }
    }

    @Test
    fun content_rejectsMaxSmallerThanMin() {
        assertThrows(IllegalArgumentException::class.java) {
            TheoTableColumnWidth.Content(
                min = 120.dp,
                max = 80.dp,
            )
        }
    }

    @Test
    fun sampled_rejectsNonPositiveCount() {
        assertThrows(IllegalArgumentException::class.java) {
            TheoTableContentWidthStrategy.Sampled(count = 0)
        }
    }
}
