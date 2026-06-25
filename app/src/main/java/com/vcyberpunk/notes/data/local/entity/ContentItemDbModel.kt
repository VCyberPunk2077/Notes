package com.vcyberpunk.notes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "content",
    primaryKeys = ["noteId", "order"],
    foreignKeys = [
        ForeignKey(
            entity = NoteDbModel::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContentItemDbModel(
    val noteId: Int,
    val type: ContentType,
    val content: String,
    val order: Int
)


enum class ContentType {

    TEXT, IMAGE

}