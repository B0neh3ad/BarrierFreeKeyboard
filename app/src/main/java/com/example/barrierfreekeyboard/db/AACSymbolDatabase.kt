package com.example.barrierfreekeyboard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.barrierfreekeyboard.model.AACSymbol
import com.example.barrierfreekeyboard.model.Converters

@Database(entities = [AACSymbol::class], version = 1)
@TypeConverters(Converters::class)
abstract class AACSymbolDatabase: RoomDatabase() {

    abstract fun aacSymbolDao(): AACSymbolDao
}