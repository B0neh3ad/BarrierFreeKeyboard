package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.databinding.KeyboardDefaultBinding
import com.example.barrierfreekeyboard.databinding.KeyboardItemBinding
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.KeyboardService
import com.example.barrierfreekeyboard.ui.PrefKeys
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import com.example.barrierfreekeyboard.ui.keyline.English
import com.example.barrierfreekeyboard.ui.keyline.KeyLine
import timber.log.Timber

class KeyboardEnglish(
    context: Context,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard<KeyboardDefaultBinding>(context, keyboardInteractionListener) {

    private var useNumPad = preference.getBoolean(PrefKeys.KB_USE_NUM_PAD, true)

    private var maxLine = if (useNumPad) 5 else 4
    private var maxLineRange = 0 until maxLine

    private var layoutLines = arrayOfNulls<LinearLayout>(if (useNumPad) 5 else 4)

    private val keys = English(useNumPad)

    private var buttons: MutableList<Button> = mutableListOf()
    private var downView: View? = null
    private var capsView: ImageView? = null

    /** init keyboard **/
    override fun init(){
        Timber.d(this.javaClass.simpleName + ":init")
        keyboardLayout = KeyboardDefaultBinding.inflate(layoutInflater)

        height = preference.getInt(PrefKeys.KB_HEIGHT, KeyboardConstants.KB_DEFAULT_HEIGHT)
        sound = preference.getInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
        vibrate = preference.getInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
        initialInterval = preference.getInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
        normalInterval = preference.getInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)

        // init LinearLayout in each line
        for(i in maxLineRange){
            val row = LinearLayout(context)
            row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height/maxLine).toDips(), 1f)
            row.id = View.generateViewId()
            if(i == 2){
                row.setPadding((12f).toDips(), 0, (12f).toDips(), 0)
            }
            keyboardLayout.root.addView(row)
            layoutLines[i] = row
        }

        // Set height in landscape mode
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            val landLine = if (useNumPad) 1..3 else 0..2
            for(i in landLine){
                layoutLines[i]?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * 0.7).toInt())
            }
        }

        setLayoutComponents()
    }

    /** Caps Lock 여부에 따라 alphabet case 전환 **/
    private fun modeChange(){
        when(capsStatus){
            Caps.OFF -> {
                capsStatus = Caps.ON
                capsView?.setImageResource(R.drawable.ic_caps_lock)
                for(button in buttons){
                    for(item in keys.flatten()){
                        if(button.text.toString() == item.normal && item.caps != null){
                            button.text = item.caps
                        }
                    }
                }
            }
            Caps.ON -> {
                capsStatus = Caps.FIXED
                capsView?.setImageResource(R.drawable.ic_caps_lock_fixed)
            }
            Caps.FIXED -> {
                clearCaps()
            }
        }
    }

    private fun clearCaps() {
        capsStatus = Caps.OFF
        capsView?.setImageResource(R.drawable.ic_caps_unlock)
        for(button in buttons){
            for(item in keys.flatten()) {
                if(button.text.toString() == item.caps) {
                    button.text = item.normal
                }
            }
        }
    }

    override fun onInputConnectionReady(inputConnection: InputConnection) {}

    override fun onKeyClickEvent(view: View?, key: KeyLine.Item) {
        when(key.normal.uppercase()) {
            "SPACE" -> runSpaceAction()
            "DEL" -> runDeleteAction()
            "CAPS" -> runCapsAction()
            "ENTER" -> runEnterAction()
            "한/영" -> {
                keyboardInteractionListener.modechange(KeyboardConstants.KB_KOR)
                clearCaps()
            }
            "!#1" -> {
                keyboardInteractionListener.modechange(KeyboardConstants.KB_SYM)
                clearCaps()
            }
            "\uD83D\uDE00" -> keyboardInteractionListener.modechange(KeyboardConstants.KB_EMO)
            else -> runNormalAction(key)
        }
    }

    override fun onKeyLongClickEvent(view: View?, key: KeyLine.Item): Boolean {
        when(key.normal.uppercase()) {
            "SPACE" -> runSpaceAction()
            "DEL" -> runDeleteAction()
            "CAPS" -> runCapsAction()
            "ENTER" -> runEnterAction()
            "한/영" -> {
                keyboardInteractionListener.modechange(KeyboardConstants.KB_KOR)
                clearCaps()
            }
            "!#1" -> {
                keyboardInteractionListener.modechange(KeyboardConstants.KB_SYM)
                clearCaps()
            }
            "\uD83D\uDE00" -> keyboardInteractionListener.modechange(KeyboardConstants.KB_EMO)
            else -> runLongAction(key)
        }
        return true
    }

    override fun onKeyTouchEvent(view: View?, key: KeyLine.Item, motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onKeyRepeatEvent(view: View?, key: KeyLine.Item) {
        when(key.normal.uppercase()) {
            "SPACE" -> runSpaceAction()
            "DEL" -> runDeleteAction()
        }
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
        if (useNumPad != changedUseNumPad) {
            changed = true
            useNumPad = changedUseNumPad
        }
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
            useNumPad = changedUseNumPad
            keys.useNumpad = useNumPad
            maxLine = if (useNumPad) 5 else 4
            maxLineRange = 0 until maxLine

            buttons.clear()
            layoutLines = arrayOfNulls(if (useNumPad) 5 else 4)
            downView = null
            capsView = null
            keyboardLayout.root.removeAllViews()
            init()
            KeyboardService.modeNotChange = true
            keyboardInteractionListener.modechange(KeyboardService.lastMode)
            KeyboardService.modeNotChange = false
        }
    }

    /** Layout 구성요소 설정(clicklistener, touchlistener, text, font size, ...) **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setLayoutComponents(){
        for((idx, line) in keys.withIndex()){

            for(item in line){
                val myKey = KeyboardItemBinding.inflate(layoutInflater)
                myKey.root.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1f)
                myKey.root.id = View.generateViewId()
                val actionButton = myKey.keyButton
                val longText = myKey.textLongClick
                val specialKey = myKey.specialKey
                longText.isClickable = false
                longText.isFocusable = false

                // bind each string(image) and actions with each key
                when(item.normal.uppercase()){
                    "SPACE" -> {
                        specialKey.setImageResource(R.drawable.ic_space_bar)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setBackgroundResource(R.drawable.key_background)
                        myKey.root.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 4f)
                    }
                    "DEL" -> {
                        specialKey.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
                        specialKey.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        myKey.root.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1.7f)
                    }
                    "CAPS" -> {
                        specialKey.setImageResource(R.drawable.ic_caps_unlock)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setBackgroundResource(R.drawable.key_background)
                        capsView = specialKey
                        myKey.root.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1.5f)
                    }
                    "ENTER" -> {
                        specialKey.setImageResource(R.drawable.ic_baseline_keyboard_return_24)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    "한/영" -> {
                        actionButton.text = item.normal
                        buttons.add(actionButton)
                    }
                    "!#1" -> {
                        actionButton.text = item.normal
                        buttons.add(actionButton)
                    }
                    else -> {
                        actionButton.text = item.normal.lowercase()
                        if (item.long != null && item.long.trim().isNotEmpty()) {
                            longText.text = item.long
                            longText.bringToFront()
                            longText.visibility = View.VISIBLE
                        }
                        actionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                        buttons.add(actionButton)
                    }
                }

                val handlerRunnable = object: Runnable {
                    override fun run() {
                        handler.postDelayed(this, normalInterval.toLong())
                        onKeyRepeatEvent(downView, item)
                    }
                }

                val touchEvent = View.OnTouchListener { v, event ->
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

                actionButton.setOnClickListener { onKeyClickEvent(it, item) }
                specialKey.setOnClickListener { onKeyClickEvent(it, item) }

                actionButton.setOnLongClickListener { onKeyLongClickEvent(it, item) }
                specialKey.setOnLongClickListener { onKeyLongClickEvent(it, item) }

                actionButton.setOnTouchListener(touchEvent)
                specialKey.setOnTouchListener(touchEvent)

                layoutLines[idx]?.addView(myKey.root)
            }
        }
    }

    private fun runSpaceAction() {
        playClick('ㅂ'.code)
        playVibrate()
        inputConnection?.commitText(" ",1)
    }

    private fun runDeleteAction() {
        playVibrate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            inputConnection?.deleteSurroundingTextInCodePoints(1, 0)
        }else{
            inputConnection?.deleteSurroundingText(1,0)
        }
    }

    private fun runCapsAction() {
        playVibrate()
        modeChange()
    }

    private fun runEnterAction() {
        playVibrate()
        val eventTime = SystemClock.uptimeMillis()
        inputConnection?.sendKeyEvent(
            KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD)
        )
        inputConnection?.sendKeyEvent(
            KeyEvent(SystemClock.uptimeMillis(), eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD)
        )
    }

    /** Click 처리 **/
    private fun runNormalAction(key: KeyLine.Item) {
        inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)

        playVibrate()

        val cursor: CharSequence? = inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
        if(cursor != null && cursor.length >= 2){
            // block 지정된 상태에서 key가 눌린 경우
            val eventTime = SystemClock.uptimeMillis()
            inputConnection?.finishComposingText()
            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
            inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
            inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
        }

        val insert = if (capsStatus != Caps.OFF) {
            key.caps ?: key.normal
        } else {
            key.normal
        }

        playClick(insert.toCharArray()[0].code)
        inputConnection?.commitText(insert, 1)

        if (capsStatus == Caps.ON) {
            clearCaps()
        }
    }

    /** Long Click 처리 (특수문자 입력) **/
    private fun runLongAction(key: KeyLine.Item) {
        if (key.long == null) return
        inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
        playVibrate()

        val cursor: CharSequence? = inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
        if(cursor != null && cursor.length >= 2){
            val eventTime = SystemClock.uptimeMillis()
            inputConnection?.finishComposingText()
            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
        }

        playClick(key.long.toCharArray()[0].code)
        inputConnection?.commitText(key.long, 1)
    }
}