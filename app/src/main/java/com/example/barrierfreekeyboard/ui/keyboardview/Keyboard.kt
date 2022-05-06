package com.example.barrierfreekeyboard.ui.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.media.AudioManager
import android.os.*
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import androidx.preference.PreferenceManager
import androidx.viewbinding.ViewBinding
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.keyline.KeyLine
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class Keyboard<T: ViewBinding>(
    protected var context: Context,
    protected var keyboardInteractionListener: KeyboardInteractionListener): CoroutineScope {

    protected val resources: Resources = context.resources
    protected val config: Configuration = resources.configuration
    protected val handler = Handler(Looper.getMainLooper())
    protected val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    protected val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    protected val vibrator: Vibrator
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }


    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * 키보드의 레이아웃을 설정합니다. 클래스의 제너릭을 확인하십시오.
     * @see ViewBinding
     */
    protected lateinit var keyboardLayout: T

    protected var height = 250
    protected var sound = 0
    protected var vibrate = 0
    protected var initialInterval = 500 // 첫 터치에 의한 입력 후 다음 터치까지 간격
    protected var normalInterval = 100 // initialInterval 이후의 모든 간격

    /**
     * 키보드의 레이아웃을 가져옵니다
     * @see keyboardLayout
     */
    val layout: View
    get() {
        return keyboardLayout.root
    }

    /**
     * Caps 상태를 저장합니다.
     * [Caps.ON], [Caps.OFF], [Caps.FIXED]
     */
    protected var capsStatus: Caps = Caps.OFF

    /**
     * InputConnection 을 설정합니다
     * 설정된 값이 null 이 아니라면 [onInputConnectionReady] 가 호출됩니다.
     * @see onInputConnectionReady
     */
    var inputConnection: InputConnection? = null
    set(value) {
        field = value
        if (value != null) onInputConnectionReady(value)
    }

    abstract fun init()

    /**
     * InputConnection 이 설정되었을때 호출됩니다.
     * @see inputConnection
     */
    abstract fun onInputConnectionReady(inputConnection: InputConnection)

    abstract fun onKeyClickEvent(view: View?, key: KeyLine.Item)
    abstract fun onKeyLongClickEvent(view: View?, key: KeyLine.Item): Boolean
    abstract fun onKeyTouchEvent(view: View?, key: KeyLine.Item, motionEvent: MotionEvent): Boolean
    abstract fun onKeyRepeatEvent(view: View?, key: KeyLine.Item)

    open fun onKeyboardChanged(before: Int, after: Int) {}
    open fun onKeyboardUpdate(event: Event) {}

    fun Int.toDips() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), context.resources.displayMetrics).toInt()
    fun Long.toDips() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), context.resources.displayMetrics).toInt()
    fun Float.toDips() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this, context.resources.displayMetrics).toInt()
    fun Double.toDips() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), context.resources.displayMetrics).toInt()

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Default

    /** key 누를 때 70ms 간 진동 발생 **/
    protected fun playVibrate(){
        if(vibrate > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(KeyboardConstants.VIB_INT.toLong(), vibrate))
            }
            else {
                vibrator.vibrate(KeyboardConstants.VIB_INT.toLong())
            }
        }
    }

    /** 딸깍 소리 발생 **/
    protected fun playClick(i: Int){
        if (sound > 0) {
            when (i) {
                KeyboardConstants.SPACEBAR -> audioManager.playSoundEffect(
                    AudioManager.FX_KEYPRESS_SPACEBAR,
                    sound.toFloat()
                )
                KeyboardConstants.KEYCODE_DONE, KeyboardConstants.KEYCODE_LF -> audioManager.playSoundEffect(
                    AudioManager.FX_KEYPRESS_RETURN,
                    sound.toFloat()
                )
                KeyboardConstants.KEYCODE_DELETE -> audioManager.playSoundEffect(
                    AudioManager.FX_KEYPRESS_DELETE,
                    sound.toFloat()
                )
                else -> audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, sound.toFloat())
            }
        }
    }

    fun destroyCoroutine() {
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
    }

    enum class Caps {
        ON,
        OFF,
        FIXED
    }

    enum class Event {
        OPEN,
        CLOSE
    }
}