package com.noteai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

class OllamaClient(
    private val baseUrl: String = "http://localhost:11434"
) {
    private val client = HttpClient(Java) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 120_000
        }
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun healthCheck(): Boolean {
        return try {
            client.get("$baseUrl/api/tags").status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Ollama health check failed: ${e.message}")
            false
        }
    }

    suspend fun generate(prompt: String, model: String = "deepseek-r1:latest"): String {
        return try {
            val response = client.post("$baseUrl/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(GenerateRequest.serializer(), GenerateRequest(model, prompt)))
            }
            val fullText = response.bodyAsText()
            val sb = StringBuilder()
            for (line in fullText.lines()) {
                if (line.isBlank()) continue
                try {
                    val obj = json.parseToJsonElement(line) as? kotlinx.serialization.json.JsonObject
                    obj?.get("response")?.toString()?.removeSurrounding("\"")?.let { sb.append(it) }
                } catch (_: Exception) {}
            }
            sb.toString()
        } catch (e: Exception) {
            println("Ollama generate error: ${e.message}")
            "Error: ${e.message}"
        }
    }
}
