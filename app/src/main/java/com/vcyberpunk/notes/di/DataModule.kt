package com.vcyberpunk.notes.di

import android.content.Context
import androidx.room.Room
import com.vcyberpunk.notes.data.local.db.NotesDao
import com.vcyberpunk.notes.data.local.db.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    companion object {

        private const val DB_NAME = "NotesDatabase"

        @Singleton
        @Provides
        fun provideNotesDatabase(
            @ApplicationContext context: Context,
        ): NotesDatabase = Room.databaseBuilder(
            context = context,
            klass = NotesDatabase::class.java,
            name = DB_NAME
        ).build()

        @Singleton
        @Provides
        fun provideNotesDao(
            notesDatabase: NotesDatabase,
        ): NotesDao = notesDatabase.notesDao()

    }

}