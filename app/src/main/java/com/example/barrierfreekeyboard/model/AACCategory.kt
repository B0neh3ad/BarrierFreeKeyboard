package com.example.barrierfreekeyboard.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aac_category_table")
data class AACCategory(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: String
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
