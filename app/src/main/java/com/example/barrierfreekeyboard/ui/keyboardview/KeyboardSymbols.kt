package com.example.barrierfreekeyboard.ui.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.AudioManager
import android.os.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.MainActivity
import com.example.barrierfreekeyboard.R

class KeyboardSymbols constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInteractionListener: KeyboardInteractionListener) {
    lateinit var symbolsLayout: LinearLayout
    var inputConnection: InputConnection? = null

    private var buttons: MutableList<Button> = mutableListOf()
    private val keysText = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0"),
        listOf("+","×","÷","=","/","￦","<",">","♡","☆"),
        listOf("!","@","#","~","%","^","&","*","(",")"),
        listOf("\uD83D\uDE00","-","'","\"",":",";",",","?","DEL"),
        listOf("가","한/영",",","space",".","Enter")
    )
    private val myKeysText = ArrayList<List<String>>()
    private val layoutLines = ArrayList<LinearLayout>()

    private var downView: View? = null
    private lateinit var sharedPreferences: SharedPreferences
    var animationMode: Int = 0
    private var sound = 0
    private var vibrate = 0
    private lateinit var vibrator: Vibrator

    private var isCaps: Boolean = false
    private var capsView: ImageView? = null

    /** init keyboard **/
    fun init(){
        symbolsLayout = layoutInflater.inflate(R.layout.keyboard_symbols, null) as LinearLayout
        inputConnection = inputConnection
        keyboardInteractionListener = keyboardInteractionListener
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        context = context

        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val height = sharedPreferences.getInt("keyboardHeight", 150)
        val config = context.resources.configuration
        sound = sharedPreferences.getInt("keyboardSound", -1)
        vibrate = sharedPreferences.getInt("keyboardVibrate", -1)

        val lines = arrayOfNulls<LinearLayout>(5)
        val lineViewsId = listOf(R.id.numpad_line, R.id.first_line, R.id.second_line, R.id.third_line, R.id.fourth_line)

        // init LinearLayout in each line
        for(i in lines.indices){
            lines[i] = symbolsLayout.findViewById(lineViewsId[i])
        }

        // Set height in both landscape and portrait
        var heightRate = 1.0
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            heightRate = 0.7
        }
        for(i in 1..3){
            lines[i]!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * heightRate).toInt())
        }

        // init key text
        myKeysText.clear()
        for(i in keysText.indices){
            myKeysText.add(keysText[i])
        }

        // init layoutLines
        layoutLines.clear()
        for(i in lines.indices){
            layoutLines.add(lines[i]!!)
        }

        setLayoutComponents()
    }

    fun getLayout(): LinearLayout {
        return symbolsLayout
    }

    /** Caps Lock 여부에 따라 alphabet case 전환 **/
    private fun modeChange(){
        if(isCaps){
            isCaps = false
            for(button in buttons){
                button.text = button.text.toString().lowercase()
            }
        }
        else{
            isCaps = true
            for(button in buttons){
                button.text = button.text.toString().uppercase()
            }
        }
    }

    /** 딸깍 소리 발생 **/
    private fun playClick(i: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        when (i) {
            MainActivity.SPACEBAR -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            MainActivity.KEYCODE_DONE, MainActivity.KEYCODE_LF -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            MainActivity.KEYCODE_DELETE -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, (-1).toFloat())
        }
    }

    /** key 누를 때 70ms 간 진동 발생 **/
    private fun playVibrate(){
        if(vibrate > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(MainActivity.VIB_INT.toLong(), vibrate))
            }
            else {
                vibrator.vibrate(MainActivity.VIB_INT.toLong())
            }
        }
    }

    /** Click 처리 **/
    private fun getMyClickListener(actionButton: Button): View.OnClickListener {
        val clickListener = (View.OnClickListener {
            inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)

            playVibrate()

            val cursorcs: CharSequence? = inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
            if(cursorcs != null && cursorcs.length >= 2){ // block 지정된 상태에서 key가 눌린 경우
                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.finishComposingText()
                inputConnection?.sendKeyEvent(
                    KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
            }
            else{
                when(actionButton.text.toString()) {
                    "\uD83D\uDE00" -> {
                        keyboardInteractionListener.modechange(MainActivity.KB_EMO)
                    }
                    "한/영" -> {
                        keyboardInteractionListener.modechange(MainActivity.KB_KOR)
                    }
                    "가" -> {
                        keyboardInteractionListener.modechange(MainActivity.KB_KOR)
                    }
                    else -> {
                        playClick(actionButton.text.toString().toCharArray()[0].code)
                        inputConnection?.commitText(actionButton.text, 1)
                    }
                }
            }
        })

        // Set OnClickListener of actionButton
        actionButton.setOnClickListener(clickListener)
        return clickListener
    }

    /** Touch 처리 **/
    private fun getOnTouchListener(clickListener: View.OnClickListener): View.OnTouchListener {
        val handler = Handler()
        val initialInterval = 500 // 첫 터치에 의한 입력 후 다음 터치까지 간격
        val normalInterval = 100 // initialInterval 이후의 모든 간격
        val handlerRunnable = object: Runnable {
            override fun run() {
                handler.postDelayed(this, normalInterval.toLong())
                clickListener.onClick(downView)
            }
        }
        val onTouchListener = object: View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handler.removeCallbacks(handlerRunnable)
                        handler.postDelayed(handlerRunnable, initialInterval.toLong())
                        downView = view!!
                        clickListener.onClick(view)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        return true
                    }
                }
                return false
            }
        }

        return onTouchListener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLayoutComponents(){
        for(line in layoutLines.indices){
            val children = layoutLines[line].children.toList()
            val myText = myKeysText[line]

            for(item in children.indices){
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                val specialKey = children[item].findViewById<ImageView>(R.id.special_key)
                var myOnClickListener: View.OnClickListener?

                // variables about special key
                val specialKeyText = listOf("space", "DEL", "CAPS", "Enter")
                val specialKeyImageResources = listOf(
                    R.drawable.ic_space_bar, R.drawable.del, R.drawable.ic_caps_unlock, R.drawable.ic_enter
                )
                val specialKeyOnClickListener = listOf(
                    getSpaceAction(), getDeleteAction(), getCapsAction(), getEnterAction()
                )

                // bind each string(image) and actions with each key
                if(specialKeyText.indexOf(myText[item]) != -1){
                    val idx = specialKeyText.indexOf(myText[item])
                    specialKey.setImageResource(specialKeyImageResources[idx])
                    specialKey.visibility = View.VISIBLE
                    actionButton.visibility = View.GONE
                    myOnClickListener = specialKeyOnClickListener[idx]
                    specialKey.setOnClickListener(myOnClickListener)
                    specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))

                    when(idx){
                        0 -> {
                            specialKey.setBackgroundResource(R.drawable.key_background)
                        }
                        1 -> {}
                        2 -> {
                            specialKey.setBackgroundResource(R.drawable.key_background)
                            capsView = specialKey
                        }
                        3 -> {
                            specialKey.setBackgroundResource(R.drawable.key_background)
                        }
                        else -> {}
                    }
                }
                else {
                    actionButton.text = myText[item]
                    buttons.add(actionButton)
                    myOnClickListener = getMyClickListener(actionButton)
                    actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                }
                children[item].setOnClickListener(myOnClickListener)
            }
        }
    }

    private fun getSpaceAction(): View.OnClickListener{
        return View.OnClickListener{
            playClick('ㅂ'.code)
            playVibrate()
            inputConnection?.commitText(" ",1)
        }
    }

    private fun getDeleteAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputConnection?.deleteSurroundingTextInCodePoints(1, 0)
            }else{
                inputConnection?.deleteSurroundingText(1,0)
            }
        }
    }

    private fun getCapsAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            modeChange()
        }
    }

    private fun getEnterAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            val eventTime = SystemClock.uptimeMillis()
            inputConnection?.sendKeyEvent(
                KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
            )
            inputConnection?.sendKeyEvent(
                KeyEvent(
                    SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
            )
        }
    }
}