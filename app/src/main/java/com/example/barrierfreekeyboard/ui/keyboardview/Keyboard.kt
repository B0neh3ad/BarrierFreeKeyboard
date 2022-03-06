package com.example.barrierfreekeyboard.ui.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.os.*
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.MainActivity

abstract class Keyboard constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInteractionListener: KeyboardInteractionListener){
    abstract fun init()
    abstract fun getLayout(): LinearLayout

    fun Float.toDips() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics).toInt()

    companion object {
        const val CAPS_OFF = 0
        const val CAPS_ON = 1
        const val CAPS_FIXED = 2
    }
}