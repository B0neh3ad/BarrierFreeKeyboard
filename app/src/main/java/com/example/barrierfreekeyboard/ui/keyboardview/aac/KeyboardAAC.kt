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
import com.example.barrierfreekeyboard.ui.KeyboardInteractionListener
import com.example.barrierfreekeyboard.ui.keyboardview.Keyboard


class KeyboardAAC (
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardInteractionListener: KeyboardInteractionListener
    ) : Keyboard(context, layoutInflater, keyboardInteractionListener) {
    lateinit var aacLayout: LinearLayout
    lateinit var inputConnection: InputConnection

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var symbolRecyclerViewAdapter: SymbolRecyclerViewAdapter

    private lateinit var categoryList: List<AACCategory>
    private lateinit var symbolList: List<AACSymbol>

    override fun init(){
        aacLayout = layoutInflater.inflate(R.layout.keyboard_aac, null) as LinearLayout
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)

        // TODO: fetch aac categories and files and init list

        setLayoutComponents(0)
    }

    override fun getLayout(): LinearLayout{
        return aacLayout
    }

    fun setLayoutComponents(categoryIdx: Int){
        /** sample variables **/
        val categoryCount = 5
        val symbolCount = 20
        val symbolPerLine = 4
        val sampleImgUri = Uri.parse("android.resource://com.example.barrierfreekeyboard/drawable/sample_1")

        val config = context.resources.configuration
        val keyboardHeight = sharedPreferences.getInt("keyboardHeight", 150)
        aacLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight * 5)

        // Category RecyclerView Setting
        var categoryRecyclerView = aacLayout.findViewById<RecyclerView>(R.id.category_recyclerview)
        val categoryList = ArrayList<AACCategory>()

        /** category List init */
        for(i in 0..categoryCount){
            categoryList.add(AACCategory("Category $i", sampleImgUri, listOf<Long>()))
        }

        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(context, categoryList, inputConnection, categoryRecyclerView.height)
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

        // Symbol RecyclerView Setting
        var symbolRecyclerView = aacLayout.findViewById<RecyclerView>(R.id.symbol_recyclerview)
        val symbolList = ArrayList<AACSymbol>()

        /** symbol List init */
        for(i in 0..symbolCount){
            symbolList.add(AACSymbol("Symbol$i", sampleImgUri))
        }

        symbolRecyclerViewAdapter = SymbolRecyclerViewAdapter(context, symbolList, inputConnection)
        symbolRecyclerView.adapter = symbolRecyclerViewAdapter
        val symbolLayoutManager = object: GridLayoutManager(context, symbolPerLine){
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = width / symbolPerLine
                lp?.width = width / symbolPerLine
                return super.checkLayoutParams(lp)
            }
        }
        symbolLayoutManager.isItemPrefetchEnabled = true
        symbolRecyclerView.layoutManager = symbolLayoutManager
    }
}