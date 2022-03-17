package com.example.barrierfreekeyboard.di

import android.content.Context
import androidx.room.Room
import com.example.barrierfreekeyboard.db.AACCategoryDatabase
import com.example.barrierfreekeyboard.db.AACSymbolDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** constuct a database of AAC category for the repository */
    @Singleton
    @Provides
    fun provideAACCategoryDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AACCategoryDatabase::class.java,
        "aac_category_table"
    ).build()

    /** constuct a database of AAC symbol for the repository */
    @Singleton
    @Provides
    fun provideAACSymbolDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AACSymbolDatabase::class.java,
        "aac_symbol_table"
    ).build()

    @Singleton
    @Provides
    fun provideAACCategoryDao(db: AACCategoryDatabase) = db.aacCategoryDao()

    @Singleton
    @Provides
    fun provideAACSymbolDao(db: AACSymbolDatabase) = db.aacSymbolDao()
}