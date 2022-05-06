package com.example.barrierfreekeyboard.ui.keyline

import com.example.barrierfreekeyboard.ui.keyline.KeyLine.Item

class Korean(var useNumpad: Boolean = true): KeyLine.System {

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
                Item("ㅂ", "ㅃ", "ㅃ"), Item("ㅈ", "ㅉ", "ㅉ"), Item("ㄷ", "ㄸ", "ㄸ"),
                Item("ㄱ", "ㄲ", "ㄲ"), Item("ㅅ", "ㅆ", "ㅆ"),
                Item("ㅛ", "_"), Item("ㅕ", "<"), Item("ㅑ", ">"),
                Item("ㅐ", "ㅒ", "ㅒ"), Item("ㅔ", "ㅖ", "ㅖ")
            ),
            KeyLine(
                Item("ㅁ", "!"), Item("ㄴ", "@"), Item("ㅇ", "#"),
                Item("ㄹ", "￦"), Item("ㅎ", "%"),
                Item("ㅗ", "&"), Item("ㅓ", "*"), Item("ㅏ", "("),
                Item("ㅣ", ")")
            ),
            KeyLine(
                Item("CAPS", isSpecial = true), Item("ㅋ", "-"), Item("ㅌ", "\'"),
                Item("ㅊ", "\""), Item("ㅍ", ":"),
                Item("ㅠ", ";"), Item("ㅜ", ","), Item("ㅡ", "?"),
                Item("DEL", isSpecial = true)
            ),
            KeyLine(
                Item("!#1", isSpecial = true), Item("한/영", isSpecial = true), Item("\uD83D\uDE00", isSpecial = true),
                Item("space"), Item("."), Item("Enter", isSpecial = true)
            )
        )
    }
}