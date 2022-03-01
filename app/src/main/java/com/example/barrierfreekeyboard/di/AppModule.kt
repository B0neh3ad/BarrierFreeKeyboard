package com.example.barrierfreekeyboard.di

import android.content.Context
import androidx.room.Room
import com.example.barrierfreekeyboard.db.AACDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAACDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AACDatabase::class.java,
        "aac_category_table"
    ).build() // construct a database for the repository

    @Singleton
    @Provides
    fun provideAACDao(db: AACDatabase) = db.aacDao() // implement a dao for the database
}