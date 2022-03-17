package com.example.barrierfreekeyboard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.barrierfreekeyboard.model.AACCategory
import com.example.barrierfreekeyboard.model.Converters

@Database(entities = [AACCategory::class], version = 1)
@TypeConverters(Converters::class)
abstract class AACCategoryDatabase: RoomDatabase() {

    abstract fun aacCategoryDao(): AACCategoryDao
}