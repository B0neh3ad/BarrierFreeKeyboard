package com.example.barrierfreekeyboard.ui

import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.barrierfreekeyboard.databinding.KeyboardViewBinding
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import com.example.barrierfreekeyboard.repository.AACRepository
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import com.example.barrierfreekeyboard.ui.keyboardview.aac.KeyboardAAC
import com.example.barrierfreekeyboard.ui.keyboardview.common.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class KeyboardService: InputMethodService(), CoroutineScope {

    private lateinit var keyboardView: KeyboardViewBinding

    private lateinit var keyboardKorean: KeyboardKorean
    private lateinit var keyboardEnglish: KeyboardEnglish
    private lateinit var keyboardSymbols: KeyboardSymbols
    private lateinit var keyboardEmoji: KeyboardEmoji
    private lateinit var keyboardNumpad: KeyboardNumpad
    private lateinit var keyboardAAC: KeyboardAAC

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        var isAAC = 0
        var isQwerty = 0
        var modeNotChange = false
        var lastMode = KeyboardConstants.KB_KOR
        var currentMode = KeyboardConstants.KB_KOR
        set(value) {
            if (modeNotChange) return
            if (value != field) lastMode = field
            field = value
            if (value == KeyboardConstants.KB_AAC) isAAC = 1
        }
    }

    private val keyboardInteractionListener = object: KeyboardInteractionListener {
        // TODO: inputconnection == null인 경우 처리
        override fun modechange(mode: Int) {
            val keyboardFrame = keyboardView.keyboardFrame
            currentInputConnection?.finishComposingText()
            keyboardFrame.removeAllViews()
            when(mode) {
                KeyboardConstants.KB_ENG -> {
                    // Qwerty
                    currentMode = mode
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.layout)
                    keyboardEnglish.onKeyboardChanged(lastMode, currentMode)
                }
                KeyboardConstants.KB_KOR -> {
                    if(isQwerty == 0){
                        // Qwerty
                        currentMode = mode
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.layout)
                        keyboardKorean.onKeyboardChanged(lastMode, currentMode)
                    }
                    else {
                        // 천지인 등..
                    }
                }
                KeyboardConstants.KB_SYM -> {
                    currentMode = mode
                    keyboardSymbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSymbols.layout)
                    keyboardSymbols.onKeyboardChanged(lastMode, currentMode)
                }
                KeyboardConstants.KB_EMO -> {
                    currentMode = mode
                    keyboardEmoji.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEmoji.layout)
                    keyboardEmoji.onKeyboardChanged(lastMode, currentMode)
                }
                KeyboardConstants.KB_NUM -> {
                    currentMode = mode
                    keyboardNumpad.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardNumpad.layout)
                    keyboardNumpad.onKeyboardChanged(lastMode, currentMode)
                }
                KeyboardConstants.KB_AAC -> {
                    keyboardAAC.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardAAC.layout)
                    keyboardAAC.onKeyboardChanged(lastMode, currentMode)
                }
            }
        }
    }

    @Inject
    lateinit var aacRepository: AACRepository

    var aacCategoryList = arrayListOf<AACCategory>()
    var aacSymbolList = arrayListOf<ArrayList<AACSymbol>>()

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Default

    override fun onCreate() {
        Timber.d("onCreate")
        super.onCreate()
        keyboardView = KeyboardViewBinding.inflate(layoutInflater)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        keyboardView.buttonDefault.setOnClickListener{
            keyboardInteractionListener.modechange(currentMode)
        }

        keyboardView.buttonAac.setOnClickListener{
            keyboardInteractionListener.modechange(KeyboardConstants.KB_AAC)
        }

        val dbInitFlag = sharedPreferences.getBoolean("dbInitFlag", false)
        Timber.d("$dbInitFlag")

        launch(Dispatchers.IO) {
            // 사진 복사 및 DB 초기화(비동기 실행)
            if(!dbInitFlag){
                sharedPreferences.edit {
                    putBoolean("dbInitFlag", true)
                }
                var inp: InputStream?
                var outp: OutputStream?

                try {
                    val outDir = filesDir.absolutePath

                    val categoryList = assets.list("category")
                    // folder existence check
                    val categoryOutPath = File("$outDir/category")
                    if(!categoryOutPath.exists()){
                        categoryOutPath.mkdirs()
                    }

                    // copy all categories to internal memory
                    for(categoryFile in categoryList!!){
                        val fileName = "category/$categoryFile"
                        val outFile = File(outDir, fileName)

                        inp = assets.open(fileName)
                        outp = FileOutputStream(outFile)

                        copyFile(inp, outp)

                        inp.close()
                        outp.flush()
                        outp.close()

                        // save file to DB
                        aacRepository.addCategory(AACCategory(categoryFile.substring(0, 4), outFile.absolutePath))
                    }

                    val symbolFolderList = assets.list("symbol")
                    for(categoryIdx in symbolFolderList!!.indices){
                        val category = symbolFolderList[categoryIdx]
                        val symbolList = assets.list("symbol/$category")
                        
                        // folder existence check
                        val symbolOutPath = File("$outDir/symbol/$category")
                        if(!symbolOutPath.exists()){
                            symbolOutPath.mkdirs()
                        }

                        // copy all symbol to internal memory
                        for(symbolFileIdx in symbolList!!.indices){
                            val symbolFile = symbolList[symbolFileIdx]
                            val fileName = "symbol/$category/$symbolFile"
                            val outFile = File(outDir, fileName)

                            inp = assets.open(fileName)
                            outp = FileOutputStream(outFile)

                            copyFile(inp, outp)

                            inp.close()
                            outp.flush()
                            outp.close()

                            // save file to DB /// TODO : 초반 시작시 IndexOutOfBoundsException: Index: 18, Size: 18 뜸
                            aacRepository.addSymbol(AACSymbol(KeyboardConstants.SYMBOL_TEXT[categoryIdx][symbolFileIdx] + " ", category, outFile.absolutePath))
                        }
                        Timber.d("Add ${symbolList.size} files to $category")
                    }
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
            aacCategoryList = aacRepository.getAllCategories().toCollection(ArrayList())
            for(aacCategory in aacCategoryList){
                aacSymbolList.add(aacRepository.getSymbolsInCategory(aacCategory.title).toCollection(ArrayList()))
                Timber.d("add ${aacSymbolList.last().size} items in ${aacCategory.title} to list")
            }
        }
    }

    override fun onCreateInputView(): View {
        Timber.d("onCreateInputView")

        keyboardKorean = KeyboardKorean(applicationContext, keyboardInteractionListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, keyboardInteractionListener)
        keyboardSymbols = KeyboardSymbols(applicationContext, keyboardInteractionListener)
        keyboardEmoji = KeyboardEmoji(applicationContext, keyboardInteractionListener)
        keyboardNumpad = KeyboardNumpad(applicationContext, keyboardInteractionListener)
        keyboardAAC = KeyboardAAC(applicationContext, keyboardInteractionListener, aacCategoryList, aacSymbolList)

        keyboardKorean.inputConnection = currentInputConnection
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardSymbols.inputConnection = currentInputConnection
        keyboardEmoji.inputConnection = currentInputConnection
        keyboardNumpad.inputConnection = currentInputConnection
        keyboardAAC.inputConnection = currentInputConnection

        keyboardKorean.init()
        keyboardEnglish.init()
        keyboardSymbols.init()
        keyboardEmoji.init()
        keyboardNumpad.init()
        keyboardAAC.init()

        return keyboardView.root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardKorean.onKeyboardUpdate(Keyboard.Event.OPEN)
        keyboardEnglish.onKeyboardUpdate(Keyboard.Event.OPEN)
        keyboardSymbols.onKeyboardUpdate(Keyboard.Event.OPEN)
        keyboardEmoji.onKeyboardUpdate(Keyboard.Event.OPEN)
        keyboardNumpad.onKeyboardUpdate(Keyboard.Event.OPEN)
        keyboardAAC.onKeyboardUpdate(Keyboard.Event.OPEN)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        keyboardKorean.onKeyboardUpdate(Keyboard.Event.CLOSE)
        keyboardEnglish.onKeyboardUpdate(Keyboard.Event.CLOSE)
        keyboardSymbols.onKeyboardUpdate(Keyboard.Event.CLOSE)
        keyboardEmoji.onKeyboardUpdate(Keyboard.Event.CLOSE)
        keyboardNumpad.onKeyboardUpdate(Keyboard.Event.CLOSE)
        keyboardAAC.onKeyboardUpdate(Keyboard.Event.CLOSE)
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardKorean.destroyCoroutine()
        keyboardEnglish.destroyCoroutine()
        keyboardSymbols.destroyCoroutine()
        keyboardEmoji.destroyCoroutine()
        keyboardNumpad.destroyCoroutine()
        keyboardAAC.destroyCoroutine()
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
    }

    override fun updateInputViewShown() {
        Timber.d("updateInputViewShown")
        super.updateInputViewShown()
        currentInputConnection?.finishComposingText()
        isQwerty = sharedPreferences.getInt("keyboardMode", 0)
        // 숫자 입력시 숫자패드로 전환
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardView.keyboardFrame.removeAllViews()
            currentMode = KeyboardConstants.KB_NUM
            keyboardNumpad.inputConnection = currentInputConnection
            keyboardView.keyboardFrame.addView(keyboardNumpad.layout)
        }
        else{
            keyboardInteractionListener.modechange(KeyboardConstants.KB_KOR)
        }
    }

    private fun copyFile(inp: InputStream, outp: OutputStream) {
        var buffer = ByteArray(1024)
        var read: Int
        while(true){
            read = inp.read(buffer)
            if(read == -1){
                break
            }
            outp.write(buffer, 0, read)
        }
    }
}