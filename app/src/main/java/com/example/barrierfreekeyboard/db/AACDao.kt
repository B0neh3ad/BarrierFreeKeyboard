package com.example.barrierfreekeyboard.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol

@Dao
interface AACDao {
    suspend fun addCategory(category: AACCategory)
    suspend fun addSymbol(symbol: AACSymbol)

    suspend fun deleteCategory(id: Long)
    suspend fun deleteSymbol(id: Long)

    fun getCategoryList(): LiveData<List<AACCategory>>
    fun getSymbolList(categoryId: Long): LiveData<List<AACSymbol>>
}