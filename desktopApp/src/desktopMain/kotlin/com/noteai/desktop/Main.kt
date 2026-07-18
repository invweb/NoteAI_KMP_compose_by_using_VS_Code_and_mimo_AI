package com.noteai.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.noteai.NoteAIApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NoteAI"
    ) {
        NoteAIApp()
    }
}
