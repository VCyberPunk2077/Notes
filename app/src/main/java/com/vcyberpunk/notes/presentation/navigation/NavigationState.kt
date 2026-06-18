package com.vcyberpunk.notes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun goBack() {
        navHostController.popBackStack()
    }
    fun navigateToCreateNoteScreen() {
        navHostController.navigate(Screen.CreateNote.route)
    }

    fun navigateToEditScreen(noteId: Int) {
        navHostController.navigate(Screen.EditNote.createRoute(noteId))
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState {
    return remember {
        NavigationState(navHostController)
    }
}