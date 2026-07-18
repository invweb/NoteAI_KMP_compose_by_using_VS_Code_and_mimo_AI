package com.noteai

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NoteAIApp() {
    val ollamaClient = remember { OllamaClient() }
    val scope = rememberCoroutineScope()
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var currentNote by remember { mutableStateOf<Note?>(null) }
    var aiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        statusMessage = "Checking Ollama connection..."
        try {
            val connected = ollamaClient.healthCheck()
            statusMessage = if (connected) "Ollama connected!" else "Ollama not available"
        } catch (e: Exception) {
            statusMessage = "Error: ${e.message}"
        }
    }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = "NoteAI",
                    style = MaterialTheme.typography.headlineMedium
                )

                if (statusMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val newNote = Note(
                            id = "${System.currentTimeMillis()}",
                            title = "New Note"
                        )
                        notes = notes + newNote
                        currentNote = newNote
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ New Note")
                }

                Spacer(modifier = Modifier.height(16.dp))

                notes.forEach { note ->
                    Card(
                        onClick = { currentNote = note },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = note.title,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                currentNote?.let { note ->
                    OutlinedTextField(
                        value = note.title,
                        onValueChange = { newTitle ->
                            notes = notes.map {
                                if (it.id == note.id) it.copy(title = newTitle) else it
                            }
                            currentNote = currentNote?.copy(title = newTitle)
                        },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = note.content,
                        onValueChange = { newContent ->
                            notes = notes.map {
                                if (it.id == note.id) it.copy(content = newContent) else it
                            }
                            currentNote = currentNote?.copy(content = newContent)
                        },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        maxLines = Int.MAX_VALUE
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(
                            onClick = {
                                isLoading = true
                                aiResponse = ""
                                errorMessage = ""
                                scope.launch {
                                    try {
                                        val prompt = "Summarize this note in 2-3 sentences:\n\nTitle: ${note.title}\n\nContent: ${note.content}"
                                        aiResponse = ollamaClient.generate(prompt)
                                    } catch (e: Exception) {
                                        errorMessage = "AI Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading && note.content.isNotBlank()
                        ) {
                            Text("AI: Summarize")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                aiResponse = ""
                                errorMessage = ""
                                scope.launch {
                                    try {
                                        val prompt = "Suggest 3-5 tags for this note:\n\nTitle: ${note.title}\n\nContent: ${note.content}\n\nReturn only tags separated by commas."
                                        aiResponse = ollamaClient.generate(prompt)
                                    } catch (e: Exception) {
                                        errorMessage = "AI Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading && note.content.isNotBlank()
                        ) {
                            Text("AI: Suggest Tags")
                        }
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thinking...")
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (aiResponse.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = aiResponse,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select or create a note")
                }
            }
        }
    }
}
