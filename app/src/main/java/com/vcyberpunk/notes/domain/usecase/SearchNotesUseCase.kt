package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    operator fun invoke(query: String): Flow<List<Note>> = repository.searchNotes(query)

}