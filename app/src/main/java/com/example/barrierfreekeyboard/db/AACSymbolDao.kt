package com.example.barrierfreekeyboard.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.barrierfreekeyboard.model.AACSymbol

@Dao
interface AACSymbolDao {
    @Query("SELECT * FROM aac_symbol_table")
    fun getAllSymbols(): List<AACSymbol>

    @Query("SELECT * FROM aac_symbol_table WHERE category=:category")
    fun getSymbolsInCategory(category: String): List<AACSymbol>

    @Insert(onConflict = IGNORE)
    suspend fun addSymbol(symbol: AACSymbol)

    @Query("DELETE FROM aac_symbol_table WHERE id=:id")
    suspend fun deleteSymbol(id: Long)

    // TODO: change order, ...
}