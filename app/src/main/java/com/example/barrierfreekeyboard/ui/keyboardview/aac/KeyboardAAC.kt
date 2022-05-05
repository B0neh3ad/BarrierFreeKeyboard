package com.example.barrierfreekeyboard.ui.keyboardview.aac

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import com.example.barrierfreekeyboard.repository.AACRepository
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.KeyboardService
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

class KeyboardAAC (
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardInteractionListener: KeyboardInteractionListener,
    // TODO: 3. db에 저장된 category, symbol list를 불러오고
    val aacCategoryList: ArrayList<AACCategory>,
    val aacSymbolList: ArrayList<ArrayList<AACSymbol>>
    ) : Keyboard(context, layoutInflater, keyboardInteractionListener) {

    lateinit var aacLayout: LinearLayout
    lateinit var inputConnection: InputConnection

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var symbolRecyclerViewAdapter: SymbolRecyclerViewAdapter

    override fun init(){
        Timber.d("KeyboardAAC:init")
        aacLayout = layoutInflater.inflate(R.layout.keyboard_aac, null) as LinearLayout
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)

        setLayoutComponents()
        if (aacCategoryList.isNotEmpty()) {
            setRecyclerViewComponents(aacCategoryList[0].title)
        }
    }

    override fun getLayout(): LinearLayout{
        return aacLayout
    }

    fun setLayoutComponents(){
        // TODO: categoryIdx에 맞는 symbol들만 채울 것
        val config = context.resources.configuration
        val keyboardHeight = sharedPreferences.getInt("keyboardHeight", 150)
        aacLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight * 5)

        // Category RecyclerView Setting
        var categoryRecyclerView = aacLayout.findViewById<RecyclerView>(R.id.category_recyclerview)

        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(context, aacCategoryList, inputConnection, categoryRecyclerView.height) { setRecyclerViewComponents(it) }
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

    fun setRecyclerViewComponents(category: String){
        val categoryIdx = aacCategoryList.map { it.title }.indexOf(category)
        var symbolRecyclerView = aacLayout.findViewById<RecyclerView>(R.id.symbol_recyclerview)

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
}