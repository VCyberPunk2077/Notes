package com.vcyberpunk.notes.domain.entity

sealed interface ContentItem {

    data class Text(val text: String): ContentItem

    data class Image(val url: String): ContentItem

}