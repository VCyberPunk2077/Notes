package com.vcyberpunk.notes.data.repository

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    override fun addNote(title: String, content: String, updatedAt: Long, isPinned: Boolean) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = oldList.size,
                title = title,
                content = content,
                updatedAt = updatedAt,
                isPinned = isPinned
            )
            oldList + note
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf {
                    it.id == noteId
                }
            }
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == note.id) {
                    note
                } else {
                    it
                }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { list ->
            list.filter {
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == noteId) {
                    it.copy(
                        isPinned = !it.isPinned
                    )
                } else {
                    it
                }
            }
        }
    }
}