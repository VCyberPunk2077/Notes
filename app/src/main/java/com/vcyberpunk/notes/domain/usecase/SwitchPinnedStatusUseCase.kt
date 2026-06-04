package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.repository.NotesRepository

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int) = repository.switchPinnedStatus(noteId)

}