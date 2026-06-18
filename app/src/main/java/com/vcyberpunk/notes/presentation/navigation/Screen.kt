package com.vcyberpunk.notes.presentation.navigation

sealed class Screen(
    val route: String
) {

    data object Notes: Screen(ROUTE_NOTES)
    data object CreateNote: Screen(ROUTE_CREATE_NOTE)
    data object EditNote: Screen(ROUTE_EDIT_NOTE) {
        private const val ROUTE_FOR_ARGS = "edit_note"

        fun createRoute(noteId: Int) = "$ROUTE_FOR_ARGS/${noteId}"
    }

    companion object {

        const val KEY_NOTE_ID = "note_id"

        private const val ROUTE_NOTES = "notes"
        private const val ROUTE_CREATE_NOTE = "create_note"
        private const val ROUTE_EDIT_NOTE = "edit_note/{$KEY_NOTE_ID}"

    }

}