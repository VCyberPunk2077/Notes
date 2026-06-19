package com.vcyberpunk.notes.presentation.screens.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcyberpunk.notes.data.repository.NotesRepositoryImpl
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.domain.usecase.GetAllNotesUseCase
import com.vcyberpunk.notes.domain.usecase.SearchNotesUseCase
import com.vcyberpunk.notes.domain.usecase.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(context: Context) : ViewModel() {

    private val repository = NotesRepositoryImpl.getInstance(context)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val notes = query
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { searchQuery ->
            if (searchQuery.isBlank()) {
                getAllNotesUseCase()
            } else {
                searchNotesUseCase(searchQuery)
            }
        }

    val state = combine(
        query,
        notes
    ) { rawQuery, notes ->
        val (pinnedNotes, otherNotes) = notes.partition { it.isPinned }
        NotesScreenState.Loaded(
            query = rawQuery,
            pinnedNotes = pinnedNotes,
            otherNotes = otherNotes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = NotesScreenState.Loaded(
            query = "",
            pinnedNotes = emptyList(),
            otherNotes = emptyList()
        )
    )

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.InputSearchQuery -> {
                query.update {
                    command.query
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

    data class Loaded(
        val query: String,
        val pinnedNotes: List<Note>,
        val otherNotes: List<Note>,
    ) : NotesScreenState

}