package com.example.barrierfreekeyboard.ui

import android.content.Context
import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.net.Uri
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.edit
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import com.example.barrierfreekeyboard.repository.AACRepository
import com.example.barrierfreekeyboard.ui.keyboardview.aac.KeyboardAAC
import com.example.barrierfreekeyboard.ui.keyboardview.common.*
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.*
import java.net.URI
import javax.inject.Inject

@AndroidEntryPoint
class KeyboardService: InputMethodService() {

    lateinit var keyboardView: LinearLayout
    lateinit var keyboardFrame: FrameLayout
    lateinit var defaultButton: Button
    lateinit var aacButton: Button

    lateinit var keyboardKorean: KeyboardKorean
    lateinit var keyboardEnglish: KeyboardEnglish
    lateinit var keyboardSymbols: KeyboardSymbols
    lateinit var keyboardEmoji: KeyboardEmoji
    lateinit var keyboardNumpad: KeyboardNumpad
    lateinit var keyboardAAC: KeyboardAAC

    lateinit var sharedPreferences: SharedPreferences

    var lastMode = KeyboardConstants.KB_KOR
    var isAAC = 0
    var isQwerty = 0

    val keyboardInteractionListener = object: KeyboardInteractionListener {
        // TODO: inputconnection == null인 경우 처리
        override fun modechange(mode: Int) {
            currentInputConnection.finishComposingText()
            keyboardFrame.removeAllViews()
            when(mode){
                KeyboardConstants.KB_ENG -> {
                    // Qwerty
                    lastMode = mode
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.getLayout())
                }
                KeyboardConstants.KB_KOR -> {
                    if(isQwerty == 0){
                        // Qwerty
                        lastMode = mode
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.getLayout())
                    }
                    else {
                        // 천지인 등..
                    }
                }
                KeyboardConstants.KB_SYM -> {
                    lastMode = mode
                    keyboardSymbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSymbols.getLayout())
                }
                KeyboardConstants.KB_EMO -> {
                    lastMode = mode
                    keyboardEmoji.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEmoji.getLayout())
                }
                KeyboardConstants.KB_NUM -> {
                    lastMode = mode
                    keyboardNumpad.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardNumpad.getLayout())
                }
                KeyboardConstants.KB_AAC -> {
                    keyboardAAC.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardAAC.getLayout())
                }
            }
        }
    }

    @Inject
    lateinit var aacRepository: AACRepository

    var aacCategoryList = arrayListOf<AACCategory>()
    var aacSymbolList = arrayListOf<ArrayList<AACSymbol>>()

    override fun onCreate() {
        Timber.d("onCreate")
        super.onCreate()
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)
        defaultButton = keyboardView.findViewById(R.id.button_default)
        aacButton = keyboardView.findViewById(R.id.button_aac)

        defaultButton.setOnClickListener{
            keyboardInteractionListener.modechange(lastMode)
        }

        aacButton.setOnClickListener{
            keyboardInteractionListener.modechange(KeyboardConstants.KB_AAC)
        }

        val dbInitFlag = sharedPreferences.getBoolean("dbInitFlag", false)
        Timber.d("$dbInitFlag")

        GlobalScope.launch(Dispatchers.IO) {
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
                        inp = null
                        outp.flush()
                        outp.close()
                        outp = null

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
                            inp = null
                            outp.flush()
                            outp.close()
                            outp = null

                            // save file to DB
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

        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardSymbols = KeyboardSymbols(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardEmoji = KeyboardEmoji(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardNumpad = KeyboardNumpad(applicationContext, layoutInflater, keyboardInteractionListener)
        keyboardAAC = KeyboardAAC(applicationContext, layoutInflater, keyboardInteractionListener, aacCategoryList, aacSymbolList)

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

        return keyboardView
    }

    override fun updateInputViewShown() {
        Timber.d("updateInputViewShown")
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        isQwerty = sharedPreferences.getInt("keyboardMode", 0)
        // 숫자 입력시 숫자패드로 전환
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardFrame.removeAllViews()
            lastMode = KeyboardConstants.KB_NUM
            keyboardNumpad.inputConnection = currentInputConnection
            keyboardFrame.addView(keyboardNumpad.getLayout())
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