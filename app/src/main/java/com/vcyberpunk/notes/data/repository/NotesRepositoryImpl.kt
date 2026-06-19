package com.vcyberpunk.notes.data.repository

import com.vcyberpunk.notes.data.local.db.NotesDao
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.data.mapper.toDbModel
import com.vcyberpunk.notes.data.mapper.toEntity
import com.vcyberpunk.notes.data.mapper.toListEntity
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean
    ) {
        val noteDbModel = NoteDbModel(
            id = UNDEFINED_ID,
            title = title,
            content = content,
            updatedAt = updatedAt,
            isPinned = isPinned
        )
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId = noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(note.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> = notesDao.getAllNotes().map { it.toListEntity() }

    override suspend fun getNote(noteId: Int): Note = notesDao.getNote(noteId).toEntity()

    override fun searchNotes(query: String): Flow<List<Note>> =
        notesDao.searchNotes(query).map { it.toListEntity() }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    companion object {
        private const val UNDEFINED_ID = 0

    }
}