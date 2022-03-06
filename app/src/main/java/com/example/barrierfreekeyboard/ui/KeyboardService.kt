package com.example.barrierfreekeyboard.ui

import android.content.Context
import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.ui.keyboardview.*

class KeyboardService: InputMethodService() {

    lateinit var keyboardView: LinearLayout
    lateinit var keyboardFrame: FrameLayout
    lateinit var defaultButton: Button
    lateinit var aacButton: Button
    lateinit var keyboardKorean: KeyboardKorean
    lateinit var keyboardEnglish: KeyboardEnglish
    lateinit var keyboardSymbols: KeyboardSymbols
    lateinit var keyboardEmoji: KeyboardEmoji
    lateinit var keyboardNumpad: KeyboardNumpad
    lateinit var keyboardAAC: KeyboardAAC
    lateinit var sharedPreferences: SharedPreferences
    var lastMode = MainActivity.KB_KOR
    var isAAC = 0
    var isQwerty = 0

    val keyboardInteractionListener = object: KeyboardInteractionListener {
        // TODO: inputconnection == null인 경우
        override fun modechange(mode: Int) {
            currentInputConnection.finishComposingText()
            keyboardFrame.removeAllViews()
            when(mode){
                MainActivity.KB_ENG -> {
                    // Qwerty
                    lastMode = mode
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.getLayout())
                }
                MainActivity.KB_KOR -> {
                    if(isQwerty == 0){
                        // Qwerty
                        lastMode = mode
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.getLayout())
                    }
                    else {
                        // 천지인
                        // keyboardFrame.addView(KeyboardChunjiin.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                    }
                }
                MainActivity.KB_SYM -> {
                    lastMode = mode
                    keyboardSymbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSymbols.getLayout())
                }
                MainActivity.KB_EMO -> {
                    lastMode = mode
                    keyboardEmoji.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEmoji.getLayout())
                }
                MainActivity.KB_NUM -> {
                    lastMode = MainActivity.KB_NUM
                    keyboardNumpad.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardNumpad.getLayout())
                }
                MainActivity.KB_AAC -> {
                    keyboardAAC.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardAAC.getLayout())
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)
        defaultButton = keyboardView.findViewById(R.id.button_default)
        aacButton = keyboardView.findViewById(R.id.button_aac)

        defaultButton.setOnClickListener{
            keyboardInteractionListener.modechange(lastMode)
        }

        aacButton.setOnClickListener{
            keyboardInteractionListener.modechange(MainActivity.KB_AAC)
        }

    }

    override fun onCreateInputView(): View {
        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardSymbols = KeyboardSymbols(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardEmoji = KeyboardEmoji(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardNumpad = KeyboardNumpad(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardAAC = KeyboardAAC(applicationContext, layoutInflater, keyboardInteractionListener)

        keyboardKorean.inputConnection = currentInputConnection
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardSymbols.inputConnection = currentInputConnection
        keyboardEmoji.inputConnection = currentInputConnection
        keyboardNumpad.inputConnection = currentInputConnection
        keyboardAAC.inputConnection = currentInputConnection

        keyboardKorean.init()
        keyboardEnglish.init()
        keyboardSymbols.init()
        keyboardEmoji.init()
        keyboardNumpad.init()
        keyboardAAC.init()

        return keyboardView
    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        isQwerty = sharedPreferences.getInt("keyboardMode", 0)
        // 숫자 입력시 숫자패드로 전환
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardFrame.removeAllViews()
            lastMode = MainActivity.KB_NUM
            keyboardNumpad.inputConnection = currentInputConnection
            keyboardFrame.addView(keyboardNumpad.getLayout())
        }
        else{
            keyboardInteractionListener.modechange(MainActivity.KB_KOR)
        }
    }
}