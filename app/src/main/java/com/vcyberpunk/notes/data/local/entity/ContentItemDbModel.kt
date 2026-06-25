package com.vcyberpunk.notes.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentItemDbModel {

    @Serializable
    data class Text(val text: String): ContentItemDbModel

    @Serializable
    data class Image(val url: String): ContentItemDbModel

}