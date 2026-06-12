package com.vcyberpunk.notes.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcyberpunk.notes.R
import com.vcyberpunk.notes.domain.entity.Note
import com.vcyberpunk.notes.presentation.theme.Green
import com.vcyberpunk.notes.presentation.theme.Yellow200

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
            onQueryChange = { query ->
                viewModel.processCommand(NotesCommand.InputSearchQuery(query = query))
            },
            onNoteClick = { note ->

            },
            onNoteLongClick = { note ->
                viewModel.processCommand(NotesCommand.SwitchPinnedStatus(noteId = note.id))
            }
        )
    }
}

@Composable
fun NotesContent(
    modifier: Modifier,
    state: NotesScreenState,
    onQueryChange: (String) -> Unit,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {
    when (state) {
        NotesScreenState.Initial -> {

        }

        is NotesScreenState.Loading -> {
            NotesLoadingContent(
                modifier = modifier,
                query = state.query,
                onQueryChange = { query ->
                    onQueryChange(query)
                },
            )
        }

        is NotesScreenState.Loaded -> {
            NotesLoadedContent(
                modifier = modifier,
                query = state.query,
                pinnedNotes = state.pinnedNotes,
                otherNotes = state.otherNotes,
                onQueryChange = { query ->
                    onQueryChange(query)
                },
                onNoteClick = { note ->
                    onNoteClick(note)
                },
                onNoteLongClick = { note ->
                    onNoteLongClick(note)
                }
            )
        }
    }
}

@Composable
fun NotesLoadingContent(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Title(
            modifier = Modifier.padding(vertical = 24.dp),
            text = stringResource(R.string.all_notes)
        )
        SearchBar(
            query = query,
            onQueryChange = {
                onQueryChange(it)
            }
        )
    }
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun NotesLoadedContent(
    modifier: Modifier,
    query: String,
    pinnedNotes: List<Note>,
    otherNotes: List<Note>,
    onQueryChange: (String) -> Unit,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Title(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp),
                    text = stringResource(R.string.all_notes)
                )
            }
            item {
                SearchBar(
                    query = query,
                    onQueryChange = {
                        onQueryChange(it)
                    }
                )
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.pinned)
                )
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(
                        items = pinnedNotes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            backgroundColor = Yellow200,
                            onClick = { onNoteClick(it) },
                            onLongClick = { onNoteLongClick(it) }
                        )
                    }
                }
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.others)
                )
            }
            items(
                items = otherNotes,
                key = { it.id }
            ) { note ->
                NoteCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    note = note,
                    backgroundColor = Green,
                    onClick = { onNoteClick(it) },
                    onLongClick = { onNoteLongClick(it) }
                )
            }
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_searchbar),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon_cd),
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { onLongClick(note) }
            )
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}