package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}