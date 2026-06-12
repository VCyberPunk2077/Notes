package com.vcyberpunk.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcyberpunk.notes.domain.entity.Note

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: NotesViewModel = viewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()
    Scaffold(modifier = modifier) { innerPadding ->
        NotesContent(
            modifier = modifier
                .padding(innerPadding),
            state = state.value,
            onNoteClick = { note ->
                viewModel.processCommand(NotesCommand.SwitchPinnedStatus(noteId = note.id))
            }
        )
    }
}

@Composable
fun NotesContent(
    modifier: Modifier,
    state: NotesScreenState,
    onNoteClick: (Note) -> Unit
) {
    when (state) {
        NotesScreenState.Initial -> {

        }

        is NotesScreenState.Loading -> {
            NotesLoadingContent(
                modifier = modifier,
            )
        }

        is NotesScreenState.Loaded -> {
            NotesLoadedContent(
                modifier = modifier,
                pinnedNotes = state.pinnedNotes,
                otherNotes = state.otherNotes,
                onNoteClick = { note ->
                    onNoteClick(note)
                }
            )
        }
    }
}

@Composable
fun NotesLoadingContent(
    modifier: Modifier,
) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun NotesLoadedContent(
    modifier: Modifier,
    pinnedNotes: List<Note>,
    otherNotes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = pinnedNotes,
                key = { it.id }
            ) { note ->
                NoteCard(
                    modifier = Modifier.fillMaxWidth(),
                    note = note,
                    onNoteClick = {
                        onNoteClick(it)
                    }
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = otherNotes,
                key = { it.id }
            ) { note ->
                NoteCard(
                    modifier = Modifier.fillMaxWidth(),
                    note = note,
                    onNoteClick = {
                        onNoteClick(it)
                    }
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
) {
    Text(
        modifier = modifier
            .clickable {
                onNoteClick(note)
            },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp
    )
}