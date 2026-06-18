package com.vcyberpunk.notes.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vcyberpunk.notes.presentation.navigation.Screen.Companion.KEY_NOTE_ID

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    notesScreenContent: @Composable () -> Unit,
    createNoteScreenContent: @Composable () -> Unit,
    editNoteScreenContent: @Composable (Int) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Notes.route
    ) {
        composable(
            route = Screen.Notes.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            notesScreenContent()
        }
        composable(
            route = Screen.CreateNote.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            createNoteScreenContent()
        }
        composable(
            route = Screen.EditNote.route,
            arguments = listOf(
                navArgument(name = KEY_NOTE_ID) {
                    type = NavType.IntType
                }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val noteId = it.arguments?.getInt(KEY_NOTE_ID) ?: throw RuntimeException("note_id is required")
            editNoteScreenContent(noteId)

        }
    }
}