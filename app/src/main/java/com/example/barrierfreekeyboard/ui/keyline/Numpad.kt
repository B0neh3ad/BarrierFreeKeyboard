package com.example.barrierfreekeyboard.ui.keyline

import com.example.barrierfreekeyboard.ui.keyline.KeyLine.Item

class Numpad: KeyLine.System {

    private val keyLines: List<KeyLine> = lines

    override fun get(line: Int): KeyLine {
        return keyLines[line]
    }

    override fun iterator(): Iterator<KeyLine> {
        return keyLines.iterator()
    }

    companion object {
        private val lines = listOf(
            KeyLine(
                Item("1"), Item("2"), Item("3"), Item("DEL", isSpecial = true)
            ),
            KeyLine(
                Item("4"), Item("5"), Item("6"), Item("Enter", isSpecial = true)
            ),
            KeyLine(
                Item("7"), Item("8"), Item("9"), Item("-", isSpecial = true)
            ),
            KeyLine(
                Item("", isSpecial = true), Item("0"), Item(",", isSpecial = true), Item(".", isSpecial = true)
            )
        )
    }
}