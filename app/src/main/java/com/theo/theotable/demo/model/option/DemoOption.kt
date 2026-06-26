package com.theo.theotable.demo.model.option

enum class DemoWidthMode { Content, Fixed }

data class DemoColumnOptions(
    val widthMode: DemoWidthMode = DemoWidthMode.Content,
    val fixedWidthDp: Float = 140f,
    val maxLines: Int = 1,
    val sortable: Boolean = true,
    val headerTint: Boolean = false,
    val cellTint: Boolean = false,
)
