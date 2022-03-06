package com.example.barrierfreekeyboard.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class AACSymbol(
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: Uri
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
}
