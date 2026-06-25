package com.vcyberpunk.notes.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.domain.entity.ContentItem
import com.vcyberpunk.notes.domain.usecase.AddNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

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
                        val newContent = prevState.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(text = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        prevState.copy(
                            content = newContent,
                        )
                    } else {
                        prevState
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
                        prevState
                    }
                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState is CreateNoteState.Creation) {
                        val content = currentState.content.filter {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                        addNoteUseCase(
                            title = currentState.title,
                            content = content
                        )
                        _events.emit(CreateNoteEvent.NavigateBack)
                    }
                }
            }

            is CreateNoteCommand.AddImage -> {
                _state.update { prevState ->
                    if (prevState is CreateNoteState.Creation) {
                        prevState.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.text.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))
                        }.let {
                            prevState.copy(
                                content = it.toList()
                            )
                        }
                    } else {
                        prevState
                    }
                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update { prevState ->
                    if (prevState is CreateNoteState.Creation) {
                        prevState.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            prevState.copy(
                                content = it.toList()
                            )
                        }
                    } else {
                        prevState
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {

    data class InputTitle(val title: String) : CreateNoteCommand

    data class InputContent(val content: String, val index: Int) : CreateNoteCommand

    data class AddImage(val uri: Uri) : CreateNoteCommand

    data class DeleteImage(val index: Int): CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand

}

sealed interface CreateNoteState {

    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
    ) : CreateNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                    }
                }
            }
    }

}

sealed interface CreateNoteEvent {

    data object NavigateBack : CreateNoteEvent

}