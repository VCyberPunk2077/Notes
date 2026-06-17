package com.vcyberpunk.notes.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vcyberpunk.notes.presentation.screens.notes.NotesScreen
import com.vcyberpunk.notes.presentation.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NotesScreen(
                    onAddNoteClick = {
                        Log.d("MainActivity", "onAddNoteClick")
                    },
                    onNoteClick = {
                        Log.d("MainActivity", "onNoteClick. Note: $it")
                    }
                )
            }
        }
    }
}
