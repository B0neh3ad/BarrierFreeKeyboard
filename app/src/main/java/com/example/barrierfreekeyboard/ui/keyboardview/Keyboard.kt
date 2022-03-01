package com.example.barrierfreekeyboard.ui.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.os.*
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

    lateinit var keyboardLayout: LinearLayout
    var inputConnection: InputConnection? = null
    lateinit var keysText: List<List<String>>

    private var buttons: MutableList<Button> = mutableListOf()
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

    /** key 누를 때 70ms 간 진동 발생 **/
    protected fun playVibrate(){
        if(vibrate > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(MainActivity.VIB_INT.toLong(), vibrate))
            }
            else {
                vibrator.vibrate(MainActivity.VIB_INT.toLong())
            }
        }
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
}