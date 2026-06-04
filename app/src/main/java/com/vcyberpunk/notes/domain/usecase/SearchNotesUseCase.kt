package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class SearchNotesUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(query: String): Flow<List<Note>> = repository.searchNotes(query)

}