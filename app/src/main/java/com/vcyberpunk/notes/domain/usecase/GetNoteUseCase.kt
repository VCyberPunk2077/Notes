package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository

class GetNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int): Note = repository.getNote(noteId)

}