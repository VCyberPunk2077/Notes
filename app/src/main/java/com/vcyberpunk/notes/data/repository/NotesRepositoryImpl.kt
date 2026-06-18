package com.vcyberpunk.notes.data.repository

import android.content.Context
import com.vcyberpunk.notes.data.local.db.NotesDatabase
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.data.mapper.toDbModel
import com.vcyberpunk.notes.data.mapper.toEntity
import com.vcyberpunk.notes.data.mapper.toListEntity
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(
    context: Context
) : NotesRepository {

    private val notesDatabase = NotesDatabase.getInstance(context)
    private val notesDao = notesDatabase.notesDao()

    override suspend fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean
    ) {
        val noteDbModel = NoteDbModel(
            id = 0,
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

        private val LOCK = Any()
        private var INSTANCE: NotesRepositoryImpl? = null

        fun getInstance(context: Context): NotesRepositoryImpl {
            INSTANCE?.let { return it }
            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val repository = NotesRepositoryImpl(
                    context = context
                )
                return repository.apply {
                    INSTANCE = this
                }
            }
        }

    }
}