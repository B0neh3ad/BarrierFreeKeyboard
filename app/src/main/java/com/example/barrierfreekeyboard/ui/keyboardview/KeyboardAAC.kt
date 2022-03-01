package com.example.barrierfreekeyboard.ui.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener

class KeyboardAAC constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInteractionListener: KeyboardInteractionListener) {
    lateinit var aacLayout: LinearLayout
    lateinit var inputConnection: InputConnection

    var sound = 0
    var vibrate = 0
    lateinit var vibrator: Vibrator

    private lateinit var aacCategoryRecyclerViewAdapter: EmojiRecyclerViewAdapter
    private lateinit var aacRecyclerViewAdapter: AACRecyclerViewAdapter

    fun init(){
        aacLayout = layoutInflater.inflate(R.layout.keyboard_aac, null) as LinearLayout
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        vibrate = sharedPreferences.getInt("vibrate", -1)
        sound = sharedPreferences.getInt("sound", -1)
        TODO("Not Yet Implemented")
        // init Linearlayout in each line
        // Set height in both landscape and portrait
        // init key text
        // init layoutLines
    }

    fun getLayout(): LinearLayout{
        return aacLayout
    }

    private fun setLayoutComponents(){
        TODO("Not Yet Implemented")
    }


    /*
    lateinit var emojiLayout: LinearLayout
    lateinit var inputConnection: InputConnection

    private val fourthLineText = listOf("한/영",getEmojiByUnicode(0x1F600), getEmojiByUnicode(0x1F466), getEmojiByUnicode(0x1F91A), getEmojiByUnicode(0x1F423),getEmojiByUnicode(0x1F331), getEmojiByUnicode(0x1F682), "DEL")

    var sound = 0
    var vibrate = 0
    lateinit var vibrator: Vibrator

    private lateinit var emojiRecyclerViewAdapter: EmojiRecyclerViewAdapter

    fun init(){
        emojiLayout = layoutInflater.inflate(R.layout.keyboard_emoji, null) as LinearLayout
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        vibrate = sharedPreferences.getInt("vibrate", -1)
        sound = sharedPreferences.getInt("sound", -1)

        setLayoutComponents(0x1F600, 79)
    }

    fun getLayout(): LinearLayout {
        return emojiLayout
    }

    private fun playVibrate(){
        if(vibrate > 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(70, vibrate))
            }
            else{
                vibrator.vibrate(70)
            }
        }
    }

    private fun setLayoutComponents(unicode: Int, count:Int) {
        // Emoji Category Setting
        val children = emojiLayout.findViewById<LinearLayout>(R.id.fourth_line).children.toList()
        for(item in children.indices){
            val actionButton = children[item].findViewById<Button>(R.id.key_button)
            val specialKey = children[item].findViewById<ImageView>(R.id.special_key)
            if(fourthLineText[item] == "DEL"){
                actionButton.setBackgroundResource(R.drawable.del)
                val myOnClickListener = getDeleteAction()
                actionButton.setOnClickListener(myOnClickListener)
            }
            else{
                actionButton.text = fourthLineText[item]
                actionButton.setOnClickListener {
                    when((it as Button).text){
                        "한/영" -> {
                            keyboardInteractionListener.modechange(1)
                        }
                        getEmojiByUnicode(0x1F600) -> {
                            setLayoutComponents(0x1F600, 79)
                        }
                        getEmojiByUnicode(0x1F466) -> {
                            setLayoutComponents(0x1F466, 88)
                        }
                        getEmojiByUnicode(0x1F91A) -> {
                            setLayoutComponents(0x1F91A, 88)
                        }
                        getEmojiByUnicode(0x1F423) -> {
                            setLayoutComponents(0x1F423, 35)
                        }
                        getEmojiByUnicode(0x1F331) -> {
                            setLayoutComponents(0x1F331, 88)
                        }
                        getEmojiByUnicode(0x1F682) -> {
                            setLayoutComponents(0x1F682, 64)
                        }
                    }
                }
            }
        }

        // RecyclerView Setting
        var recyclerView = emojiLayout.findViewById<RecyclerView>(R.id.emoji_recyclerview)
        val emojiList = ArrayList<String>()
        val config = context.resources.configuration
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val height = sharedPreferences.getInt("keyboardHeight", 150)

        for(i in 0..count){
            emojiList.add(getEmojiByUnicode(unicode + i))
        }

        emojiRecyclerViewAdapter = EmojiRecyclerViewAdapter(context, emojiList, inputConnection)
        recyclerView.adapter = emojiRecyclerViewAdapter
        val gm = GridLayoutManager(context,8)
        gm.isItemPrefetchEnabled = true
        recyclerView.layoutManager = gm
        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height * 5)
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun getDeleteAction(): View.OnClickListener {
        return View.OnClickListener {
            playVibrate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputConnection.deleteSurroundingTextInCodePoints(1, 0)
            } else {
                inputConnection.deleteSurroundingText(1, 0)
            }
        }
    }
    */
}