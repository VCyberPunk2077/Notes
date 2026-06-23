package com.vcyberpunk.notes.presentation.screens.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.usecase.DeleteNoteUseCase
import com.vcyberpunk.notes.domain.usecase.EditNoteUseCase
import com.vcyberpunk.notes.domain.usecase.GetNoteUseCase
import com.vcyberpunk.notes.presentation.screens.editing.EditNoteState.Editing
import com.vcyberpunk.notes.presentation.screens.editing.EditNoteState.Initial
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

    private val _state = MutableStateFlow<EditNoteState>(Initial)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditNoteEvent>()
    val events = _events.asSharedFlow()

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Init -> {
                viewModelScope.launch {
                    val note = getNoteUseCase(noteId = noteId)

                    _state.update {
                        Editing(note)
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
                    if (prevState is Editing) {
                        val newContent = listOf(ContentItem.Text(command.content))
                        val newNote = prevState.note.copy(content = newContent)
                        prevState.copy(note = newNote)
                    } else {
                        prevState
                    }
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { prevState ->
                    if (prevState is Editing) {
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

                    if (currentState is Editing) {
                        val note = currentState.note
                        editNoteUseCase(note = note)
                        _events.emit(EditNoteEvent.NavigateBack)
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is Editing) {
                        val note = currentState.note
                        deleteNoteUseCase(noteId = note.id)
                        _events.emit(EditNoteEvent.NavigateBack)
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

    data class InputContent(val content: String) : EditNoteCommand

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