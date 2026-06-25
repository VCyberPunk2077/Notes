package com.vcyberpunk.notes.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vcyberpunk.notes.data.local.entity.NoteDbModel

@Database(entities = [NoteDbModel::class], version = 2, exportSchema = false)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao

}