package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.databinding.KeyboardNumpadBinding
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.KeyboardService
import com.example.barrierfreekeyboard.ui.PrefKeys
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import com.example.barrierfreekeyboard.ui.keyline.KeyLine
import com.example.barrierfreekeyboard.ui.keyline.Numpad
import timber.log.Timber

class KeyboardNumpad(
    context: Context,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard<KeyboardNumpadBinding>(context, keyboardInteractionListener) {

    private var maxLineRange = 0 until 4

    private var downView: View? = null

    private var buttons: MutableList<Button> = mutableListOf()
    private var layoutLines: MutableList<LinearLayout> = mutableListOf()

    private val keys = Numpad()

    override fun init() {
        Timber.d(this.javaClass.simpleName + ":init")
        keyboardLayout = KeyboardNumpadBinding.inflate(layoutInflater)

        height = preference.getInt(PrefKeys.KB_HEIGHT, KeyboardConstants.KB_DEFAULT_HEIGHT)
        sound = preference.getInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
        vibrate = preference.getInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
        initialInterval = preference.getInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
        normalInterval = preference.getInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)

        val lines = arrayOfNulls<LinearLayout>(4)
        val lineViewsId =
            listOf(R.id.first_line, R.id.second_line, R.id.third_line, R.id.fourth_line)
        // init LinearLayout in each line
        for (i in lines.indices) {
            lines[i] = keyboardLayout.root.findViewById(lineViewsId[i])
        }

        // Set height in both landscape and portrait
        val heightRate =
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.7 else 1.0
        for (i in lines.indices) {
            lines[i]!!.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (height * heightRate).toInt()
            )
        }

        // init layoutLines
        layoutLines.clear()
        for (i in lines.indices) {
            layoutLines.add(lines[i]!!)
        }

        setLayoutComponents()
    }

    override fun onKeyboardUpdate(event: Event) {
        if (event == Event.CLOSE) return
        val changedUseNumPad = preference.getBoolean(PrefKeys.KB_USE_NUM_PAD, KeyboardConstants.KB_DEFAULT_USE_NUMPAD)
        val changedHeight = preference.getInt(PrefKeys.KB_HEIGHT, KeyboardConstants.KB_DEFAULT_HEIGHT)
        val changedSound = preference.getInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
        val changedVibrate = preference.getInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
        val changedInitialInterval = preference.getInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
        val changedNormalInterval = preference.getInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)

        var changed = false
        if (height != changedHeight) {
            changed = true
            height = changedHeight
        }
        if (sound != changedSound) {
            changed = true
            sound = changedSound
        }
        if (vibrate != changedVibrate) {
            changed = true
            vibrate = changedVibrate
        }
        if (initialInterval != changedInitialInterval) {
            changed = true
            initialInterval = changedInitialInterval
        }
        if (normalInterval != changedNormalInterval) {
            changed = true
            normalInterval = changedNormalInterval
        }

        if (changed) {
            val maxLine = if (changedUseNumPad) 5 else 4
            maxLineRange = 0 until maxLine

            buttons.clear()
            layoutLines.clear()
            downView = null
            keyboardLayout.root.removeAllViews()
            init()
            KeyboardService.modeNotChange = true
            keyboardInteractionListener.modechange(KeyboardService.lastMode)
            KeyboardService.modeNotChange = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLayoutComponents() {
        for((idx, line) in keys.withIndex()){
            val children = layoutLines[idx].children.toList()
            for((childIdx, item) in line.withIndex()){
                val actionButton = children[childIdx].findViewById<Button>(R.id.key_button)
                val specialKey = children[childIdx].findViewById<ImageView>(R.id.special_key)
                specialKey.isClickable = false
                specialKey.isFocusable = false
                actionButton.text = item.normal

                buttons.add(actionButton)

                when(item.normal.uppercase()){
                    "DEL" -> {
                        specialKey.setImageResource(R.drawable.del)
                        specialKey.visibility = View.VISIBLE
                        actionButton.setTextColor(Color.TRANSPARENT)
                    }
                }

                val handlerRunnable = object: Runnable {
                    override fun run() {
                        handler.postDelayed(this, normalInterval.toLong())
                        onKeyRepeatEvent(downView, item)
                    }
                }

                actionButton.setOnClickListener { onKeyClickEvent(it, item) }
                actionButton.setOnLongClickListener { onKeyLongClickEvent(it, item) }
                actionButton.setOnTouchListener { v, event ->
                    when(event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            handler.removeCallbacks(handlerRunnable)
                            handler.postDelayed(handlerRunnable, initialInterval.toLong())
                            downView = v
                            v.background = AppCompatResources.getDrawable(context, R.drawable.pressed)
                        }
                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            handler.removeCallbacks(handlerRunnable)
                            downView = null
                            v.background = AppCompatResources.getDrawable(context, R.drawable.normal)
                        }
                    }
                    onKeyTouchEvent(v, item, event)
                }
            }
        }
    }

    override fun onInputConnectionReady(inputConnection: InputConnection) {}
    override fun onKeyClickEvent(view: View?, key: KeyLine.Item) {
        playVibrate()

        when (key.normal.uppercase()) {
            "DEL" -> {
                inputConnection?.deleteSurroundingText(1, 0)
            }
            "ENTER" -> {
                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        eventTime, eventTime,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                        KeyEvent.FLAG_SOFT_KEYBOARD
                    )
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        SystemClock.uptimeMillis(), eventTime,
                        KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                        KeyEvent.FLAG_SOFT_KEYBOARD
                    )
                )
            }
            else -> {
                playClick(key.normal.toCharArray()[0].code)
                inputConnection?.commitText(key.normal, 1)
            }
        }
    }
    override fun onKeyLongClickEvent(view: View?, key: KeyLine.Item): Boolean = true
    override fun onKeyTouchEvent(
        view: View?,
        key: KeyLine.Item,
        motionEvent: MotionEvent
    ): Boolean = false
    override fun onKeyRepeatEvent(view: View?, key: KeyLine.Item) {
        when (key.normal.uppercase()) {
            "DEL" -> {
                inputConnection?.deleteSurroundingText(1, 0)
            }
        }
    }
}