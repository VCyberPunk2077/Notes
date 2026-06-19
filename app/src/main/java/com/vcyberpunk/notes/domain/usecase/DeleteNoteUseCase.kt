package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.repository.NotesRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int) = repository.deleteNote(noteId)

}