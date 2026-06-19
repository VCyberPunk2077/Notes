package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import javax.inject.Inject

class EditNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: Note) =
        repository.editNote(note.copy(updatedAt = System.currentTimeMillis()))

}