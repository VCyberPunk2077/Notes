package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.repository.NotesRepository

class DeleteNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int) = repository.deleteNote(noteId)

}