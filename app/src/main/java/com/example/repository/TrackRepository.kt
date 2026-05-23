package com.example.repository

import com.example.data.TrackDao
import com.example.data.TrackEntity
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.GenerationConfig
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class TrackRepository(private val trackDao: TrackDao) {
    val allTracks: Flow<List<TrackEntity>> = trackDao.getAllTracks()

    suspend fun insertTrack(track: TrackEntity): Long {
        return trackDao.insertTrack(track)
    }

    suspend fun deleteTrackById(id: Int) {
        trackDao.deleteTrackById(id)
    }

    suspend fun clearHistory() {
        trackDao.clearHistory()
    }

    suspend fun generateTrackLyrics(prompt: String, style: String, apiKey: String): String {
        val model = "gemini-3.5-flash"
        
        val systemInstructionText = """
            You are a creative Sinhala Songwriter and AI Music Creator. 
            Your goal is to write high-quality, emotionally resonant or rhythmically matching Sinhala song lyrics based on the user's concept and their musical style.
            The lyrics should be written in beautiful, poetic, or stylish Sinhala script, and matches the selected musical style (like EDM, LoFi Chill, Trap Rap, Sad, Hindi Remix, etc).
            Ensure you format the output clearly with structural labels like [Verse 1], [Chorus], [Verse 2], [Bridge], [Chorus] written clearly.
            Do not include any English explanations, only write the lyrics in beautiful Sinhala.
            Write lyrics that flow beautifully, about 3-4 verses with choruses.
        """.trimIndent()

        val userPrompt = "Create a Sinhala $style song about: \"$prompt\""
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.8f,
                maxOutputTokens = 1500
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
        )

        val response = RetrofitClient.service.generateContent(model, apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
            ?: throw Exception("No lyrics generated from Gemini. Please make sure the input concept is appropriate and your API key is valid.")
    }
}
