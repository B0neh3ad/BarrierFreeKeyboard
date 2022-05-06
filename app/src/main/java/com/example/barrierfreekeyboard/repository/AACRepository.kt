package com.example.barrierfreekeyboard.repository

import com.example.barrierfreekeyboard.db.AACCategoryDao
import com.example.barrierfreekeyboard.db.AACSymbolDao
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.AACSymbol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AACRepository @Inject constructor(private val aacCategoryDao: AACCategoryDao, private val aacSymbolDao: AACSymbolDao) {
    fun getAllCategories(): List<AACCategory> = aacCategoryDao.getAllCategories()
    suspend fun addCategory(aacCategory: AACCategory) = aacCategoryDao.addCategory(aacCategory)
    suspend fun deleteCategory(id: Long) = aacCategoryDao.deleteCategory(id)

    fun getAllSymbols(): List<AACSymbol> = aacSymbolDao.getAllSymbols()
    fun getSymbolsInCategory(category: String): List<AACSymbol> = aacSymbolDao.getSymbolsInCategory(category)
    suspend fun addSymbol(aacSymbol: AACSymbol) = aacSymbolDao.addSymbol(aacSymbol)
    suspend fun deleteSymbol(id: Long) = aacSymbolDao.deleteSymbol(id)
}