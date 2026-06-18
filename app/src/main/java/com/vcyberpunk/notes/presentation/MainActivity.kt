package com.vcyberpunk.notes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vcyberpunk.notes.presentation.navigation.AppNavGraph
import com.vcyberpunk.notes.presentation.navigation.rememberNavigationState
import com.vcyberpunk.notes.presentation.screens.creation.CreateNoteScreen
import com.vcyberpunk.notes.presentation.screens.editing.EditNoteScreen
import com.vcyberpunk.notes.presentation.screens.notes.NotesScreen
import com.vcyberpunk.notes.presentation.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                val navigationState = rememberNavigationState()
                AppNavGraph(
                    navHostController = navigationState.navHostController,
                    notesScreenContent = {
                        NotesScreen(
                            onAddNoteClick = {
                                navigationState.navigateToCreateNoteScreen()
                            },
                            onNoteClick = { note ->
                                navigationState.navigateToEditScreen(note.id)
                            }
                        )
                    },
                    createNoteScreenContent = {
                        CreateNoteScreen(
                            onFinished = {
                                navigationState.goBack()
                            }
                        )
                    },
                    editNoteScreenContent = { noteId ->
                        EditNoteScreen(
                            noteId = noteId,
                            onFinished = {
                                navigationState.goBack()
                            }
                        )
                    }
                )

            }
        }
    }
}
