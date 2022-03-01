package com.example.barrierfreekeyboard.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class AACSymbol(
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: String
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
}
