package com.example.barrierfreekeyboard.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.barrierfreekeyboard.model.AACCategory

@Dao
interface AACCategoryDao {
    @Query("SELECT * FROM aac_category_table")
    fun getAllCategories(): List<AACCategory>

    @Insert(onConflict = IGNORE)
    suspend fun addCategory(category: AACCategory)

    @Query("DELETE FROM aac_category_table WHERE id=:id")
    suspend fun deleteCategory(id: Long)

    // TODO: delete, change order, ...
}