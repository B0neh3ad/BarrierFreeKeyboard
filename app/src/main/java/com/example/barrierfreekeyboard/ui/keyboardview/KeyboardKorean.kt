package com.example.barrierfreekeyboard.ui.keyboardview

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
import androidx.core.view.children
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.R
import java.lang.NumberFormatException
import com.example.barrierfreekeyboard.ui.MainActivity

class KeyboardKorean constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInteractionListener: KeyboardInteractionListener) {

    lateinit var koreanLayout: LinearLayout
    lateinit var hangulMaker: HangulMaker
    var inputConnection: InputConnection? = null

    private var buttons: MutableList<Button> = mutableListOf()
    private val keysText = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0"),
        listOf("ㅂ","ㅈ","ㄷ","ㄱ","ㅅ","ㅛ","ㅕ","ㅑ","ㅐ","ㅔ"),
        listOf("ㅁ","ㄴ","ㅇ","ㄹ","ㅎ","ㅗ","ㅓ","ㅏ","ㅣ"),
        listOf("CAPS","ㅋ","ㅌ","ㅊ","ㅍ","ㅠ","ㅜ","ㅡ","DEL"),
        listOf("!#1","한/영",",","space",".","Enter")
    )
    private val longClickKeysText = listOf(
        listOf("!","@","#","$","%","^","&","*","(",")"),
        listOf("~","+","-","×","♥",":",";","'","\""),
        listOf("∞","_","<",">","/",",","?")
    )
    private val myKeysText = ArrayList<List<String>>()
    private val myLongClickKeysText = ArrayList<List<String>>()
    private val layoutLines = ArrayList<LinearLayout>()

    private var downView: View? = null
    private lateinit var sharedPreferences: SharedPreferences // for get/set height
    private var sound = 0
    private var vibrate = 0
    private lateinit var vibrator: Vibrator

    private var isCaps: Boolean = false
    private var capsView: ImageView? = null

    /** init keyboard **/
    fun init(){
        koreanLayout = layoutInflater.inflate(R.layout.keyboard_default, null) as LinearLayout
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)

        val height = sharedPreferences.getInt("keyboardHeight", 150)
        val config = context.resources.configuration
        sound = sharedPreferences.getInt("keyboardSound", -1)
        vibrate = sharedPreferences.getInt("keyboardVibrate", -1)

        val lines = arrayOfNulls<LinearLayout>(5)
        val lineViewsId = listOf(R.id.numpad_line, R.id.first_line, R.id.second_line, R.id.third_line, R.id.fourth_line)

        // init LinearLayout in each line
        for(i in lines.indices){
            lines[i] = koreanLayout.findViewById(lineViewsId[i])
        }

        // Set height in both landscape and portrait
        var heightRate = if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.7 else 1.0
        for(i in 1..3){
            lines[i]!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * heightRate).toInt())
        }

        // init key text
        myKeysText.clear()
        for(i in keysText.indices){
            myKeysText.add(keysText[i])
        }

        // init long click key text
        myLongClickKeysText.clear()
        for(i in longClickKeysText.indices){
            myLongClickKeysText.add(longClickKeysText[i])
        }

        // init layoutLines
        layoutLines.clear()
        for(i in lines.indices){
            layoutLines.add(lines[i]!!)
        }

        setLayoutComponents()
    }

    fun getLayout(): LinearLayout {
        hangulMaker = HangulMaker(inputConnection!!)
        setLayoutComponents()
        return koreanLayout
    }

    /** CAPS LOCK 여부에 따라 쌍자음/모음 여부 변환 **/
    private fun modeChange(){
        val normal = listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅐ", "ㅔ")
        val double = listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅒ", "ㅖ")
        if(isCaps){ // CAPS LOCK 끄기
            isCaps = false
            capsView?.setImageResource(R.drawable.ic_caps_unlock)
            for(button in buttons){
                for(i in double.indices){
                    if(button.text.toString() == double[i]){
                        button.text = normal[i]
                    }
                }
            }
        }
        else { // CAPS LOCK 켜기
            isCaps = true
            capsView?.setImageResource(R.drawable.ic_caps_lock)
            for(button in buttons){
                for(i in normal.indices){
                    if(button.text.toString() == normal[i]){
                        button.text = double[i]
                    }
                }
            }
        }
    }

    /** 딸깍 소리 발생 **/
    private fun playClick(i: Int){
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
                hangulMaker.clear()
            }
            playClick(actionButton.text.toString().toCharArray()[0].code)
            try{
                val myText = Integer.parseInt(actionButton.text.toString())
                hangulMaker.directlyCommit()
                inputConnection?.commitText(actionButton.text.toString(), 1)
            } catch (e: NumberFormatException){
                hangulMaker.commit(actionButton.text.toString().toCharArray()[0])
            }
            if(isCaps){
                modeChange()
            }
        })
        // Set OnClickListener of actionButton
        actionButton.setOnClickListener(clickListener)
        return clickListener
    }

    private fun getOnTouchListener(clickListener: View.OnClickListener): View.OnTouchListener {
        val handler = Handler(Looper.getMainLooper())
        val initialInterval = 500 // 첫 터치에 의한 입력 후 다음 터치까지 간격
        val normalInterval = 100 // initialInterval 이후의 모든 간격
        val handlerRunnable = object: Runnable {
            override fun run() {
                handler.postDelayed(this, normalInterval.toLong())
                clickListener.onClick(downView)
            }
        }
        val onTouchListener = object:View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.getAction()) {
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
            var longCLickIndex = 0

            for(item in children.indices){
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                val specialKey = children[item].findViewById<ImageView>(R.id.special_key)
                var myOnClickListener: View.OnClickListener?

                // variables about special key
                val specialKeyText = listOf("space", "DEL", "CAPS", "Enter", "한/영", "!#1")
                val specialKeyImageResources = listOf(
                    R.drawable.ic_space_bar, R.drawable.del, R.drawable.ic_caps_unlock, R.drawable.ic_enter
                )
                val specialKeyOnClickListener = listOf(
                    getSpaceAction(), getDeleteAction(), getCapsAction(), getEnterAction(),
                    object: View.OnClickListener{
                        override fun onClick(p0: View?) {
                            keyboardInteractionListener.modechange(MainActivity.KB_ENG)
                        }
                    },
                    object: View.OnClickListener{
                        override fun onClick(p0: View?) {
                            keyboardInteractionListener.modechange(MainActivity.KB_SYM)
                        }
                    }
                )

                // bind each string(image) and actions with each key
                if(specialKeyText.indexOf(myText[item]) != -1){
                    val idx = specialKeyText.indexOf(myText[item])
                    myOnClickListener = specialKeyOnClickListener[idx]
                    specialKey.setOnClickListener(myOnClickListener)
                    if(idx < 4) {
                        specialKey.setImageResource(specialKeyImageResources[idx])
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                    }
                    when(idx){
                        0 -> {
                            specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                            specialKey.setBackgroundResource(R.drawable.key_background)
                        }
                        1 -> {
                            specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        }
                        2 -> {
                            specialKey.setBackgroundResource(R.drawable.key_background)
                            capsView = specialKey
                        }
                        3 -> {
                            specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                            specialKey.setBackgroundResource(R.drawable.key_background)
                        }
                        else -> {
                            actionButton.text = myText[item]
                            buttons.add(actionButton)
                            actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        }
                    }
                }
                else {
                    actionButton.text = myText[item]
                    actionButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.toFloat())
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
            hangulMaker.commitSpace()
        }
    }

    private fun getDeleteAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            val cursorcs: CharSequence? = inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
            if(cursorcs != null && cursorcs.length >= 2){ //
                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.finishComposingText()
                inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                hangulMaker.clear()
            }
            else{
                hangulMaker.delete()
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
            hangulMaker.directlyCommit()
            val eventTime = SystemClock.uptimeMillis()
            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
            inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD))
        }
    }
}