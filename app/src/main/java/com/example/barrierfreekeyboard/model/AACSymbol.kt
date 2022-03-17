package com.example.barrierfreekeyboard.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aac_symbol_table")
data class AACSymbol(
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: Uri
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
