package com.vcyberpunk.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.data.repository.TestNotesRepositoryImpl
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.usecase.GetAllNotesUseCase
import com.vcyberpunk.notes.domain.usecase.SearchNotesUseCase
import com.vcyberpunk.notes.domain.usecase.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel : ViewModel() {

    private val repository = TestNotesRepositoryImpl
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow<NotesScreenState>(NotesScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        query
            .distinctUntilChanged {old, new ->
                old == new
            }
            .flatMapLatest { query ->
                val notesFlow = if (query.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(query)
                }

                notesFlow
                    .map<List<Note>, NotesScreenState> { notes ->
                        val (pinnedNotes, otherNotes) = notes.partition { it.isPinned }
                        NotesScreenState.Loaded(
                            query = query,
                            pinnedNotes = pinnedNotes,
                            otherNotes = otherNotes
                        )
                    }
                    .onStart {
                        emit(
                            NotesScreenState.Loading(
                                query = query
                            )
                        )
                    }
            }
            .onEach { notesScreenState ->
                _state.value = notesScreenState
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.InputSearchQuery -> {
                query.update {
                    command.query.trim()
                }
            }

            is NotesCommand.SwitchPinnedStatus -> {
                viewModelScope.launch {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }

    }

}

sealed interface NotesCommand {

    data class InputSearchQuery(
        val query: String
    ) : NotesCommand

    data class SwitchPinnedStatus(
        val noteId: Int
    ) : NotesCommand

}

sealed interface NotesScreenState {

    data object Initial : NotesScreenState

    data class Loading(
        val query: String
    ) : NotesScreenState

    data class Loaded(
        val query: String,
        val pinnedNotes: List<Note>,
        val otherNotes: List<Note>,
    ) : NotesScreenState

}