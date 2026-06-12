package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}