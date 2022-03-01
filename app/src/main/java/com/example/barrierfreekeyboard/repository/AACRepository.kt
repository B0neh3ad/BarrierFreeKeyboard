package com.example.barrierfreekeyboard.repository

import androidx.lifecycle.LiveData
import com.example.barrierfreekeyboard.db.AACDao
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import javax.inject.Inject

class AACRepository @Inject constructor(
    private val aacDao: AACDao
) {
    suspend fun addCategory(category: AACCategory) = aacDao.addCategory(category)
    suspend fun addSymbol(symbol: AACSymbol) = aacDao.addSymbol(symbol)

    suspend fun deleteCategory(id: Long) = aacDao.deleteCategory(id)
    suspend fun deleteSymbol(id: Long) = aacDao.deleteSymbol(id)

    fun getCategoryList(): LiveData<List<AACCategory>> = aacDao.getCategoryList()
    fun getSymbolList(categoryId: Long): LiveData<List<AACSymbol>> = aacDao.getSymbolList(categoryId)
}