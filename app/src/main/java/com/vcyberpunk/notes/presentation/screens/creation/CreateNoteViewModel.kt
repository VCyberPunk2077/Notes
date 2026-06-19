package com.vcyberpunk.notes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.data.repository.NotesRepositoryImpl
import com.vcyberpunk.notes.domain.usecase.AddNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel(context: Context): ViewModel() {

    private val repository = NotesRepositoryImpl.getInstance(context)
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateNoteEvent>()
    val events = _events.asSharedFlow()

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Back -> {
                viewModelScope.launch {
                    _events.emit(CreateNoteEvent.NavigateBack)
                }
            }
            is CreateNoteCommand.InputContent -> {
                _state.update { prevState ->
                    if (prevState is CreateNoteState.Creation) {
                        prevState.copy(
                            content = command.content,
                        )
                    } else {
                        CreateNoteState.Creation(content = command.content)
                    }
                }
            }
            is CreateNoteCommand.InputTitle -> {
                _state.update { prevState ->
                    if (prevState is CreateNoteState.Creation) {
                        prevState.copy(
                            title = command.title,
                        )
                    } else {
                        CreateNoteState.Creation(title = command.title)
                    }
                }
            }
            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is CreateNoteState.Creation) {
                        addNoteUseCase(
                            title = currentState.title,
                            content = currentState.content
                        )

                        _events.emit(CreateNoteEvent.NavigateBack)
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {

    data class InputTitle(val title: String): CreateNoteCommand

    data class InputContent(val content: String): CreateNoteCommand

    data object Save: CreateNoteCommand

    data object Back: CreateNoteCommand

}

sealed interface CreateNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
    ): CreateNoteState {
        val isSaveEnabled: Boolean
            get() = title.isNotBlank() && content.isNotBlank()
    }

}

sealed interface CreateNoteEvent {

    data object NavigateBack: CreateNoteEvent

}