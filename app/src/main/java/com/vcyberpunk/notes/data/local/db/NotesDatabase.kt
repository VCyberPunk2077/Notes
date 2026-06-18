package com.vcyberpunk.notes.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vcyberpunk.notes.data.local.entity.NoteDbModel

@Database(entities = [NoteDbModel::class], version = 1, exportSchema = false)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        private const val DB_NAME = "NotesDatabase"
        private var INSTANCE: NotesDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): NotesDatabase {
            INSTANCE?.let { return it }
            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val database = Room.databaseBuilder(
                    context = context,
                    klass = NotesDatabase::class.java,
                    name = DB_NAME
                ).build()
                return database.apply {
                    INSTANCE = this
                }
            }
        }

    }
}