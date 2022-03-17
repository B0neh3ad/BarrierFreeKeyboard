package com.example.barrierfreekeyboard.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import com.example.barrierfreekeyboard.model.AACSymbol

@Dao
interface AACSymbolDao {
    @Insert(onConflict = IGNORE)
    suspend fun addSymbol(symbol: AACSymbol)
    // TODO: delete, change order, ...
}