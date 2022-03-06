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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.R
import java.lang.NumberFormatException
import com.example.barrierfreekeyboard.ui.MainActivity

class KeyboardKorean (
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard(context, layoutInflater, keyboardInteractionListener) {

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

    private var isCaps: Int = CAPS_OFF
    private var capsView: ImageView? = null

    /** init keyboard **/
    override fun init(){
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

        // init LinearLayout in each line
        for(i in 0..4){
            val row = LinearLayout(context)
            row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (50f).toDips(), 1f)
            row.id = View.generateViewId()
            if(i == 2){
                row.setPadding((12f).toDips(), 0, (12f).toDips(), 0)
            }
            koreanLayout.addView(row)
            lines[i] = row
        }

        // Set height in landscape mode
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

    override fun getLayout(): LinearLayout {
        hangulMaker = HangulMaker(inputConnection!!)
        return koreanLayout
    }

    /** CAPS LOCK 여부에 따라 쌍자음/모음 여부 변환 **/
    private fun modeChange(){
        val normal = listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅐ", "ㅔ")
        val double = listOf("ㅃ", "ㅉ", "ㄸ", "ㄲ", "ㅆ", "ㅒ", "ㅖ")
        when(isCaps){
            CAPS_OFF -> {
                isCaps = CAPS_ON
                capsView?.setImageResource(R.drawable.ic_caps_lock)
                for(button in buttons){
                    for(i in normal.indices){
                        if(button.text.toString() == normal[i]){
                            button.text = double[i]
                        }
                    }
                }
            }
            CAPS_ON -> {
                isCaps = CAPS_OFF
                capsView?.setImageResource(R.drawable.ic_caps_unlock)
                for(button in buttons){
                    for(i in double.indices){
                        if(button.text.toString() == double[i]){
                            button.text = normal[i]
                        }
                    }
                }
            }
            else -> {}
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
            if(isCaps == CAPS_ON){
                modeChange()
            }
        })
        // Set OnClickListener of actionButton
        actionButton.setOnClickListener(clickListener)
        return clickListener
    }

    /** Touch 처리 **/
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
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handler.removeCallbacks(handlerRunnable)
                        handler.postDelayed(handlerRunnable, initialInterval.toLong())
                        downView = view!!
                        view.background = context.getDrawable(R.drawable.pressed)
                        clickListener.onClick(view)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        view!!.background = context.getDrawable(R.drawable.normal)
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        view!!.background = context.getDrawable(R.drawable.normal)
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
            // var longClickIndex = 0

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
                        myOnClickListener = View.OnClickListener { keyboardInteractionListener.modechange(MainActivity.KB_ENG) }
                        specialKey.setOnClickListener(myOnClickListener)
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                    }
                    "!#1" -> {
                        myOnClickListener = View.OnClickListener { keyboardInteractionListener.modechange(MainActivity.KB_SYM) }
                        specialKey.setOnClickListener(myOnClickListener)
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
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