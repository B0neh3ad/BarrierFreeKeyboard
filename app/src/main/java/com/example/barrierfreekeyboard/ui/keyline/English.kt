package com.example.barrierfreekeyboard.ui.keyline

import com.example.barrierfreekeyboard.ui.keyline.KeyLine.Item

class English(var useNumpad: Boolean = true): KeyLine.System {

    private val keyLines: List<KeyLine>
        get() {
            return mutableListOf<KeyLine>().apply {
                if (useNumpad) add(number)
                addAll(lines)
            }
        }

    override fun get(line: Int): KeyLine {
        return keyLines[line]
    }

    override fun iterator(): Iterator<KeyLine> {
        return keyLines.iterator()
    }

    companion object {
        private val number = KeyLine("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        private val lines = listOf(
            KeyLine(
                Item("q", "+", "Q"), Item("w", "×", "W"), Item("e", "÷", "E"),
                Item("r", "=", "R"), Item("t", "/", "T"),
                Item("y", "_", "Y"), Item("u", "<", "U"), Item("i", ">", "I"),
                Item("o", "[", "O"), Item("p", "]", "P")
            ),
            KeyLine(
                Item("a", "!", "A"), Item("s", "@", "S"), Item("d", "#", "D"),
                Item("f", "$", "F"), Item("g", "%", "G"),
                Item("h", "&", "H"), Item("j", "*", "J"), Item("k", "(", "K"),
                Item("l", ")", "L")
            ),
            KeyLine(
                Item("CAPS", isSpecial = true), Item("z", "-", "Z"), Item("x", "\'", "X"),
                Item("c", "\"", "C"), Item("v", ":", "V"),
                Item("b", ";", "B"), Item("n", ",", "N"), Item("m", "?", "M"),
                Item("DEL", isSpecial = true)
            ),
            KeyLine(
                Item("!#1", isSpecial = true), Item("한/영", isSpecial = true), Item("\uD83D\uDE00", isSpecial = true),
                Item("space"), Item("."), Item("Enter", isSpecial = true)
            )
        )
    }
}