package com.example.barrierfreekeyboard.db

import androidx.room.Dao
import com.example.barrierfreekeyboard.model.AACCategory

@Dao
interface AACCategoryDao {
    suspend fun addCategory(category: AACCategory)
    // TODO: delete, change order, ...
}