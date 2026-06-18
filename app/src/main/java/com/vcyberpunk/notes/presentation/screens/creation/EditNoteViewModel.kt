package com.vcyberpunk.notes.presentation.screens.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.data.TestNotesRepositoryImpl
import com.vcyberpunk.notes.domain.usecase.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel: ViewModel() {

    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Initial)
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Init -> {
                _state.update { CreateNoteState.Creation() }
            }
            CreateNoteCommand.Back -> {
                _state.update { CreateNoteState.Finished }
            }
            is CreateNoteCommand.InputContent -> {
                _state.update { prevState ->
                    if (prevState is CreateNoteState.Creation) {
                        prevState.copy(
                            content = command.content,
                            isSaveEnabled = prevState.title.isNotBlank() && command.content.isNotBlank()
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
                            isSaveEnabled = prevState.content.isNotBlank() && command.title.isNotBlank()
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

                        _state.update { CreateNoteState.Finished }
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {

    data object Init : CreateNoteCommand

    data class InputTitle(val title: String): CreateNoteCommand

    data class InputContent(val content: String): CreateNoteCommand

    data object Save: CreateNoteCommand

    data object Back: CreateNoteCommand

}

sealed interface CreateNoteState {

    data object Initial: CreateNoteState

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ): CreateNoteState

    data object Finished: CreateNoteState

}