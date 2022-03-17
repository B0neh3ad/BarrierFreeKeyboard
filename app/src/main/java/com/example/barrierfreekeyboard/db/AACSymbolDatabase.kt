package com.example.barrierfreekeyboard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.barrierfreekeyboard.model.AACSymbol

@Database(entities = [AACSymbol::class], version = 1)
abstract class AACSymbolDatabase: RoomDatabase() {

    abstract fun aacSymbolDao(): AACSymbolDao
}