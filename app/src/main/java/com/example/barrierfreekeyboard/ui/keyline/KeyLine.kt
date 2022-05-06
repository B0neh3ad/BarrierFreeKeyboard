package com.example.barrierfreekeyboard.ui.keyline

class KeyLine: Collection<KeyLine.Item> {
    private var line: List<Item>

    constructor(vararg items: Item) {
        line = items.asList()
    }
    constructor(vararg items: String) {
        line = items.map { Item(normal = it) }
    }

    val test = ArrayList<String>()
    override fun iterator(): Iterator<Item> = line.iterator()

    data class Item(
        val normal: String,
        val long: String? = null,
        val caps: String? = null,
        val isSpecial: Boolean = false
    )

    interface System: Iterable<KeyLine> {
        operator fun get(line: Int): KeyLine
    }

    operator fun get(index: Int): Item {
        return line[index]
    }

    override val size: Int
        get() = line.size

    override fun contains(element: Item): Boolean {
        return line.contains(element)
    }

    override fun containsAll(elements: Collection<Item>): Boolean {
        return line.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return line.isEmpty()
    }
}