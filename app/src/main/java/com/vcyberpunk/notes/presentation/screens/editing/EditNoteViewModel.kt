package com.vcyberpunk.notes.presentation.screens.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.data.repository.NotesRepositoryImpl
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.usecase.DeleteNoteUseCase
import com.vcyberpunk.notes.domain.usecase.EditNoteUseCase
import com.vcyberpunk.notes.domain.usecase.GetNoteUseCase
import com.vcyberpunk.notes.presentation.screens.editing.EditNoteState.Editing
import com.vcyberpunk.notes.presentation.screens.editing.EditNoteState.Initial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(context: Context, private val noteId: Int) : ViewModel() {

    private val repository = NotesRepositoryImpl.getInstance(context)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)

    private val _state = MutableStateFlow<EditNoteState>(Initial)
    val state = _state.asStateFlow()

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
                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { prevState ->
                    if (prevState is Editing) {
                        val newNote = prevState.note.copy(content = command.content)
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
                        _state.update { EditNoteState.Finished }
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is Editing) {
                        val note = currentState.note
                        deleteNoteUseCase(noteId = note.id)
                        _state.update { EditNoteState.Finished }
                    }
                }
            }
        }
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
            get() = note.title.isNotBlank() && note.content.isNotBlank()

    }

    data object Finished : EditNoteState

}