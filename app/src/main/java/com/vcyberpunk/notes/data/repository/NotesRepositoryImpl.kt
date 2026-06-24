package com.vcyberpunk.notes.data.repository

import com.vcyberpunk.notes.data.local.ImageFileManager
import com.vcyberpunk.notes.data.local.db.NotesDao
import com.vcyberpunk.notes.data.mapper.toDbModel
import com.vcyberpunk.notes.data.mapper.toEntity
import com.vcyberpunk.notes.data.mapper.toListEntity
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        updatedAt: Long,
        isPinned: Boolean
    ) {
        val note = Note(
            id = UNDEFINED_ID,
            title = title,
            content = content.processedForStorage(),
            updatedAt = updatedAt,
            isPinned = isPinned
        )
        val noteDbModel = note.toDbModel()
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId = noteId)

        note.content
            .filterIsInstance<ContentItem.Image>()
            .map { it.url }
            .forEach {
                imageFileManager.deleteImage(it)
            }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = notesDao.getNote(note.id).toEntity()
        val oldUrls = oldNote.content
            .filterIsInstance<ContentItem.Image>()
            .map { it.url }

        val newUrls = note.content
            .filterIsInstance<ContentItem.Image>()
            .map { it.url }
        val removedUrls = oldUrls - newUrls.toSet()
        removedUrls.forEach {
            imageFileManager.deleteImage(it)
        }

        val processedContent = note.content.processedForStorage()
        val processedNote = note.copy(content = processedContent)

        notesDao.addNote(processedNote.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> = notesDao.getAllNotes().map { it.toListEntity() }

    override suspend fun getNote(noteId: Int): Note = notesDao.getNote(noteId).toEntity()

    override fun searchNotes(query: String): Flow<List<Note>> =
        notesDao.searchNotes(query).map { it.toListEntity() }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    private suspend fun List<ContentItem>.processedForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(
                            url = internalPath
                        )
                    }
                }

                is ContentItem.Text -> contentItem
            }
        }
    }

    companion object {
        private const val UNDEFINED_ID = 0

    }
}