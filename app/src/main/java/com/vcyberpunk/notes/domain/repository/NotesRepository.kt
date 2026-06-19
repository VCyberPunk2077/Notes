package com.vcyberpunk.notes.domain.repository

import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun addNote(title: String, content: List<ContentItem>, updatedAt: Long, isPinned: Boolean)

    suspend fun deleteNote(noteId: Int)

    suspend fun editNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getNote(noteId: Int): Note

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun switchPinnedStatus(noteId: Int)

}