package com.theo.theotable.sample

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.theo.theotable.R
import com.theo.theotable.demo.model.table.DemoTableColumnStyle
import com.theo.theotable.demo.model.table.DemoTableData
import com.theo.theotable.demo.model.table.DemoTableHeader
import com.theo.theotable.demo.model.table.DemoTableRow
import org.json.JSONObject

fun Context.loadLargeTableData(): DemoTableData {
    val json = resources.openRawResource(R.raw.large_table)
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }

    val data = JSONObject(json).getJSONObject("data")
    val headersJson = data.getJSONArray("headers")

    val headers = List(headersJson.length()) { index ->
        val item = headersJson.getJSONObject(index)
        DemoTableHeader(
            key = item.getString("key"),
            name = item.getString("name"),
            style = largeTableColumnStyle(index)
        )
    }

    val contentsJson = data.getJSONArray("contents")
    val rows = List(contentsJson.length()) { index ->
        val item = contentsJson.getJSONObject(index)
        val valuesJson = item.getJSONObject("values")

        DemoTableRow(
            id = item.getInt("compareMismatchId"),
            values = headers.associate { header ->
                val value = valuesJson.opt(header.key)
                header.key to if(value == null || value == JSONObject.NULL) "" else value.toString()
            },
        )
    }

    return DemoTableData(headers = headers, rows = rows)
}

private val LargeTableColumnStyles = listOf(
    DemoTableColumnStyle(
        headerBackground = Color(0xFFE8F1FF),
        cellBackground = Color(0xFFF8FBFF),
    ),
    DemoTableColumnStyle(
        headerBackground = Color(0xFFF0FDF4),
        cellBackground = Color(0xFFF7FEF9),
    ),
    DemoTableColumnStyle(
        headerBackground = Color(0xFFFFF7ED),
        cellBackground = Color(0xFFFFFBF5),
    ),
    DemoTableColumnStyle(
        headerBackground = Color(0xFFF5F3FF),
        cellBackground = Color(0xFFFAF8FF),
    ),
    DemoTableColumnStyle(
        headerBackground = Color(0xFFFDF2F8),
        cellBackground = Color(0xFFFFF8FB),
    ),
)

private fun largeTableColumnStyle(index: Int): DemoTableColumnStyle {
    return LargeTableColumnStyles[index % LargeTableColumnStyles.size]
}