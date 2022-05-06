package com.example.barrierfreekeyboard.ui.keyboardview.common

import android.content.Context
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.databinding.KeyboardEmojiBinding
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.KeyboardService
import com.example.barrierfreekeyboard.ui.PrefKeys
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import com.example.barrierfreekeyboard.ui.keyline.KeyLine
import timber.log.Timber

class KeyboardEmoji (
    context: Context,
    keyboardInteractionListener: KeyboardInteractionListener
) : Keyboard<KeyboardEmojiBinding>(context, keyboardInteractionListener) {

    private var useNumPad = preference.getBoolean(PrefKeys.KB_USE_NUM_PAD, true)

    private var maxLine = if (useNumPad) 5 else 4
    private var maxLineRange = 0 until maxLine

    private val fourthLineText = listOf("한/영",getEmojiByUnicode(0x1F600), getEmojiByUnicode(0x1F466), getEmojiByUnicode(0x1F91A), getEmojiByUnicode(0x1F423),getEmojiByUnicode(0x1F331), getEmojiByUnicode(0x1F682), "DEL")

    private lateinit var emojiRecyclerViewAdapter: EmojiRecyclerViewAdapter

    override fun init(){
        Timber.d(this.javaClass.simpleName + ":init")
        keyboardLayout = KeyboardEmojiBinding.inflate(layoutInflater)

        height = preference.getInt(PrefKeys.KB_HEIGHT, KeyboardConstants.KB_DEFAULT_HEIGHT)
        sound = preference.getInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
        vibrate = preference.getInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
        initialInterval = preference.getInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
        normalInterval = preference.getInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)

        setLayoutComponents()
        setRecyclerViewComponents(0x1F600, 79)
    }

    private fun setLayoutComponents() {
        // Emoji Category Setting
        keyboardLayout.fourthLine.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height/maxLine).toDips(), 1f)
        val children = keyboardLayout.fourthLine.children
        for((idx, item) in children.withIndex()){
            val actionButton = item.findViewById<Button>(R.id.key_button)
            val specialKey = item.findViewById<ImageView>(R.id.special_key)
            if(fourthLineText[idx] == "DEL"){
                actionButton.setBackgroundResource(R.drawable.ic_backspace)
                val myOnClickListener = getDeleteAction()
                actionButton.setOnClickListener(myOnClickListener)
            }
            else{
                actionButton.text = fourthLineText[idx]
                actionButton.setOnClickListener {
                    when((it as Button).text){
                        "한/영" -> {
                            keyboardInteractionListener.modechange(KeyboardConstants.KB_KOR)
                        }
                        getEmojiByUnicode(0x1F600) -> {
                            setRecyclerViewComponents(0x1F600, 79)
                        }
                        getEmojiByUnicode(0x1F466) -> {
                            setRecyclerViewComponents(0x1F466, 88)
                        }
                        getEmojiByUnicode(0x1F91A) -> {
                            setRecyclerViewComponents(0x1F91A, 88)
                        }
                        getEmojiByUnicode(0x1F423) -> {
                            setRecyclerViewComponents(0x1F423, 35)
                        }
                        getEmojiByUnicode(0x1F331) -> {
                            setRecyclerViewComponents(0x1F331, 88)
                        }
                        getEmojiByUnicode(0x1F682) -> {
                            setRecyclerViewComponents(0x1F682, 64)
                        }
                    }
                }
            }
        }
    }

    private fun setRecyclerViewComponents(unicode: Int, count: Int){
        // RecyclerView Setting
        val recyclerView = keyboardLayout.emojiRecyclerview
        val emojiList = ArrayList<String>()

        for(i in 0..count){
            emojiList.add(getEmojiByUnicode(unicode + i))
        }

        emojiRecyclerViewAdapter = EmojiRecyclerViewAdapter(context, emojiList, inputConnection)
        recyclerView.adapter = emojiRecyclerViewAdapter
        val gm = GridLayoutManager(context,8)
        gm.isItemPrefetchEnabled = true

        val line = maxLine - 1F
        val maxHeight = height/maxLine

        recyclerView.layoutManager = gm
        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (maxHeight * line).toDips())
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun getDeleteAction(): View.OnClickListener {
        return View.OnClickListener{
            playVibrate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputConnection?.deleteSurroundingTextInCodePoints(1, 0)
            }else{
                inputConnection?.deleteSurroundingText(1,0)
            }
        }
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
            maxLine = if (useNumPad) 5 else 4
            maxLineRange = 0 until maxLine

            keyboardLayout.root.removeAllViews()
            init()
            KeyboardService.modeNotChange = true
            keyboardInteractionListener.modechange(KeyboardService.lastMode)
            KeyboardService.modeNotChange = false
        }
    }
}