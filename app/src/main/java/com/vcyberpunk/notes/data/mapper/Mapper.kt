package com.vcyberpunk.notes.data.mapper

import com.vcyberpunk.notes.data.local.entity.ContentItemDbModel
import com.vcyberpunk.notes.data.local.entity.ContentType
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.data.local.entity.NoteWithContentDbModel
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(
        id = id,
        title = title,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun List<ContentItem>.toListContentItemDbModel(noteId: Int): List<ContentItemDbModel> =
    mapIndexed { index, contentItem ->
        when (contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    type = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }

            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    type = ContentType.TEXT,
                    content = contentItem.text,
                    order = index
                )
            }
        }
    }

fun List<ContentItemDbModel>.toListContentItem(): List<ContentItem> = map { contentItemDbModel ->
    when (contentItemDbModel.type) {
        ContentType.TEXT -> {
            ContentItem.Text(contentItemDbModel.content)
        }

        ContentType.IMAGE -> {
            ContentItem.Image(contentItemDbModel.content)
        }
    }
}

fun NoteWithContentDbModel.toEntity(): Note {
    return Note(
        id = noteDbModel.id,
        title = noteDbModel.title,
        content = contentItemDbModel.toListContentItem(),
        updatedAt = noteDbModel.updatedAt,
        isPinned = noteDbModel.isPinned
    )
}

fun List<NoteWithContentDbModel>.toListEntity(): List<Note> = map {
    it.toEntity()
}