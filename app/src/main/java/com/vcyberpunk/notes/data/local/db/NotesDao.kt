package com.vcyberpunk.notes.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteDbModel

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Upsert
    suspend fun addNote(noteDbModel: NoteDbModel)

}