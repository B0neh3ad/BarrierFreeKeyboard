package com.example.barrierfreekeyboard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.barrierfreekeyboard.model.AACCategory

@Database(entities = [AACCategory::class], version = 1)
abstract class AACCategoryDatabase: RoomDatabase() {

    abstract fun aacCategoryDao(): AACCategoryDao
}