package com.vcyberpunk.notes.presentation.screens.editing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.usecase.DeleteNoteUseCase
import com.vcyberpunk.notes.domain.usecase.EditNoteUseCase
import com.vcyberpunk.notes.domain.usecase.GetNoteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("note_id") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditNoteEvent>()
    val events = _events.asSharedFlow()

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Init -> {
                viewModelScope.launch {
                    val note = getNoteUseCase(noteId = noteId)
                    val newContent = note.content.toMutableList().apply {
                        add(ContentItem.Text(""))
                    }

                    _state.update {
                        EditNoteState.Editing(note.copy(content = newContent))
                    }
                }
            }

            EditNoteCommand.Back -> {
                viewModelScope.launch {
                    _events.emit(EditNoteEvent.NavigateBack)
                }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { prevState ->
                    if (prevState is EditNoteState.Editing) {
                        val newContent = prevState.note.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(text = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        val newNote = prevState.note.copy(
                            content = newContent
                        )
                        prevState.copy(
                            note = newNote,
                        )
                    } else {
                        prevState
                    }
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { prevState ->
                    if (prevState is EditNoteState.Editing) {
                        val newNote = prevState.note.copy(title = command.title)
                        prevState.copy(note = newNote)
                    } else {
                        prevState
                    }
                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is EditNoteState.Editing) {
                        val note = currentState.note
                        val content = note.content.filter {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                        editNoteUseCase(note = note.copy(content = content))
                        _events.emit(EditNoteEvent.NavigateBack)
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is EditNoteState.Editing) {
                        val note = currentState.note
                        deleteNoteUseCase(noteId = note.id)
                        _events.emit(EditNoteEvent.NavigateBack)
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { prevState ->
                    if (prevState is EditNoteState.Editing) {
                        prevState.note.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.text.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))
                        }.let {
                            val newNote = prevState.note.copy(
                                content = it
                            )
                            EditNoteState.Editing(
                                note = newNote
                            )
                        }
                    } else {
                        prevState
                    }
                }
            }

            is EditNoteCommand.DeleteImage -> {
                _state.update { prevState ->
                    if (prevState is EditNoteState.Editing) {
                        prevState.note.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            val newNote = prevState.note.copy(
                                content = it
                            )
                            EditNoteState.Editing(
                                note = newNote
                            )
                        }
                    } else {
                        prevState
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("note_id") noteId: Int
        ): EditNoteViewModel

    }
}

sealed interface EditNoteCommand {

    data object Init : EditNoteCommand

    data class InputTitle(val title: String) : EditNoteCommand

    data class InputContent(val content: String, val index: Int) : EditNoteCommand

    data class AddImage(val uri: Uri) : EditNoteCommand

    data class DeleteImage(val index: Int): EditNoteCommand

    data object Save : EditNoteCommand

    data object Back : EditNoteCommand

    data object Delete : EditNoteCommand

}

sealed interface EditNoteState {

    data object Initial : EditNoteState

    data class Editing(
        val note: Note
    ) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                    }
                }
            }
    }

}

sealed interface EditNoteEvent {

    data object NavigateBack : EditNoteEvent

}