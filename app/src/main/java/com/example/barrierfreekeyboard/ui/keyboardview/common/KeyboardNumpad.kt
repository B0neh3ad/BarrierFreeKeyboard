package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager
import android.os.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.MainActivity
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard

class KeyboardNumpad(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard(context, layoutInflater, keyboardInteractionListener) {

    lateinit var numpadLayout: LinearLayout
    lateinit var inputConnection: InputConnection

    var buttons: MutableList<Button> = mutableListOf<Button>()
    private val keysText = listOf(
        listOf("1", "2", "3", "DEL"),
        listOf("4", "5", "6", "Enter"),
        listOf("7", "8", "9", "."),
        listOf("-", "0", ",", "")
    )
    private val myKeysText = ArrayList<List<String>>()
    private val layoutLines = ArrayList<LinearLayout>()

    lateinit var vibrator: Vibrator

    override fun init() {
        numpadLayout = layoutInflater.inflate(R.layout.keyboard_numpad, null) as LinearLayout
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val height = sharedPreferences.getInt("keyboardHeight", 150)
        val config = context.resources.configuration

        val lines = arrayOfNulls<LinearLayout>(4)
        val lineViewsId =
            listOf(R.id.first_line, R.id.second_line, R.id.third_line, R.id.fourth_line)
        // init LinearLayout in each line
        for (i in lines.indices) {
            lines[i] = numpadLayout.findViewById(lineViewsId[i])
        }

        // Set height in both landscape and portrait
        var heightRate =
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.7 else 1.0
        for (i in lines.indices) {
            lines[i]!!.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (height * heightRate).toInt()
            )
        }

        // init key text
        myKeysText.clear()
        for (i in keysText.indices) {
            myKeysText.add(keysText[i])
        }

        // init layoutLines
        layoutLines.clear()
        for (i in lines.indices) {
            layoutLines.add(lines[i]!!)
        }

        setLayoutComponents()
    }

    override fun getLayout(): LinearLayout {
        return numpadLayout
    }

    /** 딸깍 소리 발생 **/
    private fun playClick(i: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        when (i) {
            KeyboardConstants.SPACEBAR -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            KeyboardConstants.KEYCODE_DONE, KeyboardConstants.KEYCODE_LF -> am!!.playSoundEffect(
                AudioManager.FX_KEYPRESS_RETURN
            )
            KeyboardConstants.KEYCODE_DELETE -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, (-1).toFloat())
        }
    }

    private fun setLayoutComponents() {
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val sound = sharedPreferences.getInt("keyboardSound", -1)
        val vibrate = sharedPreferences.getInt("keyboardVibrate", -1)

        for (line in layoutLines.indices) {
            val children = layoutLines[line].children.toList()
            val myText = myKeysText[line]
            for (item in children.indices) {
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                actionButton.text = myText[item]

                buttons.add(actionButton)

                val clickListener = (View.OnClickListener {
                    if (vibrate > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(70, vibrate))
                        } else {
                            vibrator.vibrate(70)
                        }
                    }

                    when (actionButton.text.toString()) {
                        "DEL" -> {
                            inputConnection.deleteSurroundingText(1, 0)
                        }
                        "Enter" -> {
                            val eventTime = SystemClock.uptimeMillis()
                            inputConnection.sendKeyEvent(
                                KeyEvent(
                                    eventTime, eventTime,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                                    KeyEvent.FLAG_SOFT_KEYBOARD
                                )
                            )
                            inputConnection.sendKeyEvent(
                                KeyEvent(
                                    SystemClock.uptimeMillis(), eventTime,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                                    KeyEvent.FLAG_SOFT_KEYBOARD
                                )
                            )
                        }
                        else -> {
                            playClick(actionButton.text.toString().toCharArray()[0].code)
                            inputConnection.commitText(actionButton.text, 1)
                        }
                    }
                })
                actionButton.setOnClickListener(clickListener)
                children[item].setOnClickListener(clickListener)
            }
        }
    }
}