package com.vcyberpunk.notes.data.mapper

import com.vcyberpunk.notes.data.local.entity.ContentItemDbModel
import com.vcyberpunk.notes.data.local.entity.NoteDbModel
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    val contentAsString = Json.encodeToString(this.content.toListContentItemDbModel())
    return NoteDbModel(
        id = id,
        title = title,
        content = contentAsString,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun List<ContentItem>.toListContentItemDbModel(): List<ContentItemDbModel> = map { contentItem ->
    when (contentItem) {
        is ContentItem.Image -> ContentItemDbModel.Image(url = contentItem.url)
        is ContentItem.Text -> ContentItemDbModel.Text(text = contentItem.text)
    }
}

fun List<ContentItemDbModel>.toListContentItem(): List<ContentItem> = map { contentItemDbModel ->
    when (contentItemDbModel) {
        is ContentItemDbModel.Image -> ContentItem.Image(url = contentItemDbModel.url)
        is ContentItemDbModel.Text -> ContentItem.Text(text = contentItemDbModel.text)
    }
}

fun NoteDbModel.toEntity(): Note {
    val contentItemDbModels =
        Json.decodeFromString<List<ContentItemDbModel>>(this.content)
    return Note(
        id = id,
        title = title,
        content = contentItemDbModels.toListContentItem(),
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun List<NoteDbModel>.toListEntity(): List<Note> = map {
    it.toEntity()
}