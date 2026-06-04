package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository

class AddNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: Note) = repository.addNote(note)

}