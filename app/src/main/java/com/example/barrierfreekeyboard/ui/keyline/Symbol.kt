package com.example.barrierfreekeyboard.ui.keyline

import com.example.barrierfreekeyboard.ui.keyline.KeyLine.Item

class Symbol(var useNumpad: Boolean = true): KeyLine.System {

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
                Item("+", null, "`"), Item("×", null, "~"), Item("÷", null, "\\"),
                Item("=", null, "|"), Item("/", null, "{"),
                Item("_", null, "}"), Item("<", null, "€"), Item(">", null, "￡"),
                Item("[", null, "￥"), Item("]", null, "$")
            ),
            KeyLine(
                Item("!", null, "º"), Item("@", null, "•"), Item("#", null, "○"),
                Item("$", null, "●"), Item("%", null, "□"),
                Item("^", null, "■"), Item("&", null, "♤"), Item("*", null, "♡"),
                Item("(", null, "◇"), Item(")", null, "♧")
            ),
            KeyLine(
                Item("1/2", null, "2/2", true), Item("-", null, "☆"), Item("\'", null, "▪︎"),
                Item("\"", null, "¤"), Item(":", null, "《"),
                Item(";", null, "》"), Item(",", null, "¡"), Item("?", null, "¿"),
                Item("DEL", isSpecial = true)
            ),
            KeyLine(
                Item("EXIT", isSpecial = true), Item("한/영", isSpecial = true), Item("\uD83D\uDE00", isSpecial = true),
                Item("space"), Item("."), Item("Enter", isSpecial = true)
            )
        )
    }
}