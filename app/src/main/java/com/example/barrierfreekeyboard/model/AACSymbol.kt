package com.example.barrierfreekeyboard.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI

@Entity(tableName = "aac_symbol_table")
data class AACSymbol(
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: String
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
