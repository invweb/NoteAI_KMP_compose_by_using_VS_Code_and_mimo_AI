# NoteAI

Cross-platform notes application with AI-powered features using Ollama.

## Screenshots

![Main Interface](screenshots/scr1.png)
![AI in Action](screenshots/scr2.png)
![Ollama Integration](screenshots/ol.png)

## Features

- Create and edit notes
- AI-powered summarization
- AI-powered tag suggestions
- Desktop (Windows, macOS, Linux) support
- Android support (coming soon)

## Requirements

- JDK 17+
- Ollama running locally on port 11434

## Setup

1. Install Ollama: https://ollama.com
2. Pull a model: `ollama pull deepseek-r1`
3. Run the application: `gradle :desktopApp:run`

## Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Ollama (local AI)
- Ktor (HTTP client)
