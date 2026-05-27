package com.theo.theotable.core

enum class SelectionMode {
    None,
    Single,
    Multiple,
}

data class TableSelection<K>(
    val selectedKeys: Set<K> = emptySet(),
    val anchorKey: K? = null,
) {
    fun isSelected(key: K): Boolean = key in selectedKeys

    fun clear(): TableSelection<K> = copy(
        selectedKeys = emptySet(),
        anchorKey = null,
    )

    fun select(key: K, mode: SelectionMode): TableSelection<K> = when(mode) {
        SelectionMode.None -> clear()
        SelectionMode.Single -> copy(
            selectedKeys = setOf(key),
            anchorKey = key,
        )
        SelectionMode.Multiple -> copy(
            selectedKeys = selectedKeys + key,
            anchorKey = key,
        )
    }

    fun toggle(key: K, mode: SelectionMode): TableSelection<K> = when(mode) {
        SelectionMode.None -> clear()
        SelectionMode.Single -> if(isSelected(key)) clear() else select(key, mode)
        SelectionMode.Multiple -> copy(
            selectedKeys = if(isSelected(key)) selectedKeys - key else selectedKeys + key,
            anchorKey = key,
        )
    }

    fun selectRange(targetKey: K, orderedKeys: List<K>, mode: SelectionMode): TableSelection<K> {
        if(mode != SelectionMode.Multiple) return select(targetKey, mode)

        val anchor = anchorKey ?: targetKey
        val from = orderedKeys.indexOf(anchor)
        val to = orderedKeys.indexOf(targetKey)

        if(from == -1 || to == -1) return select(targetKey, mode)

        val range = if(from <= to) {
            orderedKeys.subList(from, to + 1)
        } else {
            orderedKeys.subList(to, from + 1)
        }

        return copy(
            selectedKeys = selectedKeys + range,
            anchorKey = anchor,
        )
    }
}