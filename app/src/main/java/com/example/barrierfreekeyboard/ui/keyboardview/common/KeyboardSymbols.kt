package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.AudioManager
import android.os.*
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.MainActivity
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard

class KeyboardSymbols(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard(context, layoutInflater, keyboardInteractionListener) {
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
    override fun init(){
        symbolsLayout = layoutInflater.inflate(R.layout.keyboard_default, null) as LinearLayout
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

        // init LinearLayout in each line
        for(i in 0..4){
            val row = LinearLayout(context)
            row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (50f).toDips(), 1f)
            row.id = View.generateViewId()
            if(i == 2){
                row.setPadding((12f).toDips(), 0, (12f).toDips(), 0)
            }
            symbolsLayout.addView(row)
            lines[i] = row
        }

        // Set height in both landscape and portrait
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            for(i in 1..3){
                lines[i]!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * 0.7).toInt())
            }
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

    override fun getLayout(): LinearLayout {
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
            val myText = myKeysText[line]

            for(item in myText.indices){
                val myKey = layoutInflater.inflate(R.layout.keyboard_item, null) as ConstraintLayout
                myKey.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1f)
                myKey.id = View.generateViewId()
                val actionButton = myKey.findViewById<Button>(R.id.key_button)
                val specialKey = myKey.findViewById<ImageView>(R.id.special_key)
                var myOnClickListener: View.OnClickListener?

                // bind each string(image) and actions with each key
                when(myText[item]){
                    "space" -> {
                        myOnClickListener = getSpaceAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setImageResource(R.drawable.ic_space_bar)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                        myKey.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 4f)
                    }
                    "DEL" -> {
                        myOnClickListener = getDeleteAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setImageResource(R.drawable.del)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        myKey.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1.7f)
                    }
                    "CAPS" -> {
                        myOnClickListener = getCapsAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setImageResource(R.drawable.ic_caps_unlock)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                        capsView = specialKey
                        myKey.layoutParams = LinearLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_PARENT, 1.5f)
                    }
                    "Enter" -> {
                        myOnClickListener = getEnterAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setImageResource(R.drawable.ic_enter)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    "한/영" -> {
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        myOnClickListener = getMyClickListener(actionButton)
                        actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                    }
                    else -> {
                        actionButton.text = myText[item]
                        actionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                        buttons.add(actionButton)
                        myOnClickListener = getMyClickListener(actionButton)
                        actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                    }
                }
                myKey.setOnClickListener(myOnClickListener)
                layoutLines[line].addView(myKey)
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