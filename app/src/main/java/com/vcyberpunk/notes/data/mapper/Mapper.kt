package com.vcyberpunk.notes.data.mapper

import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.domain.entity.Note

fun Note.toDbModel(): NoteDbModel = NoteDbModel(
    id = id,
    title = title,
    content = content,
    updatedAt = updatedAt,
    isPinned = isPinned
)

fun NoteDbModel.toEntity(): Note = Note(
    id = id,
    title = title,
    content = content,
    updatedAt = updatedAt,
    isPinned = isPinned
)

fun List<NoteDbModel>.toListEntity(): List<Note> = map {
    it.toEntity()
}