package com.example.barrierfreekeyboard.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aac_category_table")
data class AACCategory(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image_uri")
    val imageURI: Uri,
    @ColumnInfo(name = "symbol_list")
    val symbolIdList: List<Long>
    // TODO: val soundURI(?)
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
