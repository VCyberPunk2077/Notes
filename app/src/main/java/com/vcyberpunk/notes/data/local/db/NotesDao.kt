package com.vcyberpunk.notes.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vcyberpunk.notes.data.local.entity.ContentItemDbModel
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.data.local.entity.NoteWithContentDbModel
import com.vcyberpunk.notes.data.mapper.toListContentItemDbModel
import com.vcyberpunk.notes.domain.entity.ContentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentDbModel

    @Transaction
    @Query("""
        SELECT DISTINCT notes.* FROM notes JOIN content
        ON notes.id == content.noteId
        WHERE title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
        """)
    fun searchNotes(query: String): Flow<List<NoteWithContentDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Upsert
    suspend fun addNote(noteDbModel: NoteDbModel): Long

    @Upsert
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteNoteContent(noteId: Int)

    @Transaction
    suspend fun addNoteWithContent(
        noteDbModel: NoteDbModel,
        processedContent: List<ContentItem>
    ) {
        val noteId = addNote(noteDbModel).toInt()
        val contentItems = processedContent.toListContentItemDbModel(noteId)
        addNoteContent(contentItems)
    }

    @Transaction
    suspend fun editNoteWithContent(
        noteDbModel: NoteDbModel,
        contentItems: List<ContentItemDbModel>
    ) {
        addNote(noteDbModel)
        deleteNoteContent(noteDbModel.id)
        addNoteContent(contentItems)
    }

}