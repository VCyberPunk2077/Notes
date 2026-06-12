package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.repository.NotesRepository

class AddNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(
        title: String,
        content: String
    ) = repository.addNote(
        title = title,
        content = content
    )

}