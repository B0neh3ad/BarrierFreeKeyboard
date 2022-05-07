package com.example.barrierfreekeyboard.ui.keyboardview.aac

import android.content.Context
import android.content.res.Configuration
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barrierfreekeyboard.databinding.KeyboardAacBinding
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.KeyboardService
import com.example.barrierfreekeyboard.ui.PrefKeys
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import com.example.barrierfreekeyboard.ui.keyline.KeyLine
import timber.log.Timber

class KeyboardAAC (
    context: Context,
    keyboardInteractionListener: KeyboardInteractionListener,
    // TODO: 3. db에 저장된 category, symbol list를 불러오고
    val aacCategoryList: ArrayList<AACCategory>,
    val aacSymbolList: ArrayList<ArrayList<AACSymbol>>
    ) : Keyboard<KeyboardAacBinding>(context, keyboardInteractionListener) {

    private var useNumPad = preference.getBoolean(PrefKeys.KB_USE_NUM_PAD, true)

    private var maxLine = if (useNumPad) 5 else 4
    private var maxLineRange = 0 until maxLine

    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var symbolRecyclerViewAdapter: SymbolRecyclerViewAdapter

    override fun init(){
        Timber.d(this.javaClass.simpleName + ":init")
        keyboardLayout = KeyboardAacBinding.inflate(layoutInflater)

        height = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            preference.getInt(PrefKeys.KB_PORTRAIT_HEIGHT, KeyboardConstants.KB_DEFAULT_PORTRAIT_HEIGHT)
        } else {
            preference.getInt(PrefKeys.KB_LANDSCAPE_HEIGHT, KeyboardConstants.KB_DEFAULT_LANDSCAPE_HEIGHT)
        }
        sound = preference.getInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
        vibrate = preference.getInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
        initialInterval = preference.getInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
        normalInterval = preference.getInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)

        setLayoutComponents()
        if (aacCategoryList.isNotEmpty()) {
            setRecyclerViewComponents(aacCategoryList[0].title)
        }
    }

    fun setLayoutComponents(){
        // TODO: categoryIdx에 맞는 symbol들만 채울 것

        val line = maxLine
        val maxHeight = height/maxLine

        keyboardLayout.root.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (maxHeight * line).toDips())

        // Category RecyclerView Setting

        val categoryRecyclerView = keyboardLayout.categoryRecyclerview

        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(
            context,
            aacCategoryList,
            inputConnection,
            categoryRecyclerView.height) { setRecyclerViewComponents(it) }
        categoryRecyclerView.adapter = categoryRecyclerViewAdapter
        val categoryLayoutManager = object: LinearLayoutManager(context) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = height
                lp?.width = height
                return super.checkLayoutParams(lp)
            }
        }
        categoryLayoutManager.isItemPrefetchEnabled = true
        categoryLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        categoryRecyclerView.layoutManager = categoryLayoutManager
    }

    private fun setRecyclerViewComponents(category: String){
        val categoryIdx = aacCategoryList.map { it.title }.indexOf(category)
        val symbolRecyclerView = keyboardLayout.symbolRecyclerview

        symbolRecyclerViewAdapter = SymbolRecyclerViewAdapter(context, aacSymbolList[categoryIdx], inputConnection)
        symbolRecyclerView.adapter = symbolRecyclerViewAdapter
        val symbolLayoutManager = object: GridLayoutManager(context, KeyboardConstants.SYMBOL_PER_LINE){
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = width / KeyboardConstants.SYMBOL_PER_LINE
                lp?.width = width / KeyboardConstants.SYMBOL_PER_LINE
                return super.checkLayoutParams(lp)
            }
        }
        symbolLayoutManager.isItemPrefetchEnabled = true
        symbolRecyclerView.layoutManager = symbolLayoutManager
    }

    override fun onInputConnectionReady(inputConnection: InputConnection) {}
    override fun onKeyClickEvent(view: View?, key: KeyLine.Item) {}
    override fun onKeyLongClickEvent(view: View?, key: KeyLine.Item): Boolean = true
    override fun onKeyTouchEvent(
        view: View?,
        key: KeyLine.Item,
        motionEvent: MotionEvent
    ): Boolean = true
    override fun onKeyRepeatEvent(view: View?, key: KeyLine.Item) {}

    override fun onKeyboardUpdate(event: Event) {
        if (event == Event.CLOSE) return
        val changedUseNumPad = preference.getBoolean(PrefKeys.KB_USE_NUM_PAD, KeyboardConstants.KB_DEFAULT_USE_NUMPAD)
        val changedHeight = if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            preference.getInt(PrefKeys.KB_PORTRAIT_HEIGHT, KeyboardConstants.KB_DEFAULT_PORTRAIT_HEIGHT)
        } else {
            preference.getInt(PrefKeys.KB_LANDSCAPE_HEIGHT, KeyboardConstants.KB_DEFAULT_LANDSCAPE_HEIGHT)
        }
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
            maxLine = if (useNumPad) 5 else 4
            maxLineRange = 0 until maxLine

            keyboardLayout.root.removeAllViews()
            init()
            KeyboardService.modeNotChange = true
            keyboardInteractionListener.modeChange(KeyboardService.lastMode)
            KeyboardService.modeNotChange = false
        }
    }
}