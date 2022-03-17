package com.example.barrierfreekeyboard.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import com.example.barrierfreekeyboard.model.AACCategory

@Dao
interface AACCategoryDao {
    @Insert(onConflict = IGNORE)
    suspend fun addCategory(category: AACCategory)
    // TODO: delete, change order, ...
}