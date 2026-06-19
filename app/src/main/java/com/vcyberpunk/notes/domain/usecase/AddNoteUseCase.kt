package com.vcyberpunk.notes.domain.usecase

import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.repository.NotesRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(
        title: String,
        content: List<ContentItem>,
    ) = repository.addNote(
        title = title,
        content = content,
        updatedAt = System.currentTimeMillis(),
        isPinned = false
    )

}