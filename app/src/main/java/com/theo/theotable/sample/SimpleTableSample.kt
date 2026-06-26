package com.theo.theotable.sample

import androidx.compose.ui.graphics.Color
import com.theo.theotable.demo.model.table.DemoTableColumnStyle
import com.theo.theotable.demo.model.table.DemoTableData
import com.theo.theotable.demo.model.table.DemoTableHeader
import com.theo.theotable.demo.model.table.DemoTableRow

private data class Dessert(
    val id: Int,
    val name: String,
    val calories: Int,
    val fat: Double,
    val carbs: Int,
    val protein: Double,
    val sodium: Int,
    val price: Int,
)

private val sampleDesserts = listOf(
    Dessert(1, "Cupcake", 305, 3.7, 67, 4.3, 413, 3500),
    Dessert(2, "Donut", 452, 25.0, 51, 4.9, 326, 2800),
    Dessert(3, "Eclair", 262, 16.0, 24, 6.0, 337, 4200),
    Dessert(4, "Frozen yogurt with honey topping", 159, 6.0, 24, 4.0, 87, 3900),
    Dessert(5, "Gingerbread", 356, 16.0, 49, 3.9, 327, 3200),
    Dessert(6, "Honeycomb", 408, 3.2, 87, 6.5, 562, 4500),
    Dessert(7, "Ice cream sandwich", 237, 9.0, 37, 4.3, 129, 3700),
    Dessert(8, "Jelly bean assortment pack", 375, 0.0, 94, 0.0, 50, 2500),
    Dessert(9, "KitKat", 518, 26.0, 65, 7.0, 54, 3300),
    Dessert(10, "Lollipop", 392, 0.2, 98, 0.0, 38, 1900),
    Dessert(11, "Marshmallow", 318, 0.2, 81, 1.8, 80, 2200),
    Dessert(12, "Nougat bar with roasted nuts", 360, 19.0, 43, 3.0, 210, 4100),
    Dessert(13, "Oreo", 437, 18.0, 63, 4.0, 203, 3000),
)

fun createSimpleTableData(): DemoTableData {
    val headers = listOf(
        DemoTableHeader(
            key = "name",
            name = "Name",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFFFF3D8),
                cellBackground = Color(0xFFFFFBF0),
            )
        ),
        DemoTableHeader(
            key = "calories",
            name = "Calories",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFE8F1FF),
                cellBackground = Color(0xFFF4F8FF),
            )
        ),
        DemoTableHeader(
            key = "fat",
            name = "Fat",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFFFEFEF),
                cellBackground = Color(0xFFFFF7F7),
            )
        ),
        DemoTableHeader(
            key = "carbs",
            name = "Carbs",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFEFF6FF),
                cellBackground = Color(0xFFF8FBFF),
            )
        ),
        DemoTableHeader(
            key = "protein",
            name = "Protein",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFF0FDF4),
                cellBackground = Color(0xFFF7FEF9),
            )
        ),
        DemoTableHeader(
            key = "sodium",
            name = "Sodium",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFFFF7ED),
                cellBackground = Color(0xFFFFFBF5),
            )
        ),
        DemoTableHeader(
            key = "price",
            name = "Price",
            style = DemoTableColumnStyle(
                headerBackground = Color(0xFFEAF7EA),
                cellBackground = Color(0xFFF4FBF4),
            )
        ),
    )

    val rows = sampleDesserts.map { dessert ->
        DemoTableRow(
            id = dessert.id,
            values = mapOf(
                "name" to dessert.name,
                "calories" to dessert.calories.toString(),
                "fat" to "${dessert.fat}g",
                "carbs" to "${dessert.carbs}g",
                "protein" to "${dessert.protein}g",
                "sodium" to "${dessert.sodium}mg",
                "price" to "${dessert.price}KRW",
            ),
        )
    }

    return DemoTableData(headers = headers, rows = rows)
}
