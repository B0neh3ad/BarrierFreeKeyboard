package com.example.barrierfreekeyboard.model

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.net.URI

class Converters {
    @TypeConverter
    fun longListToString(value: List<Long>?): String = Gson().toJson(value)

    @TypeConverter
    fun stringToLongList(value: String?): List<Long> = Gson().fromJson(value, Array<Long>::class.java).toList()

    @TypeConverter
    fun uriToString(value: Uri?): String = value.toString()

    @TypeConverter
    fun stringToUri(value: String?): Uri = Uri.parse(value)

    @TypeConverter
    fun javaURIToString(value: URI?): String = value.toString()

    @TypeConverter
    fun stringToJavaURI(value: String?): URI = URI(value)
}