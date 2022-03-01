package com.example.barrierfreekeyboard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.barrierfreekeyboard.model.AACCategory
import javax.inject.Singleton

@Database(entities = [AACCategory::class], version = 1)
abstract class AACDatabase: RoomDatabase() {

    abstract fun aacDao(): AACDao
}