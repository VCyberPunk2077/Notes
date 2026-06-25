package com.vcyberpunk.notes.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithContentDbModel(
    @Embedded
    val noteDbModel: NoteDbModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val contentItemDbModel: List<ContentItemDbModel>
)