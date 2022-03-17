package com.example.barrierfreekeyboard.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class AACCategoryForDb(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "thumb_path")
    val thumbPath: String,
    @ColumnInfo(name = "symbol_path")
    val symbolPath: String
){
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
}
