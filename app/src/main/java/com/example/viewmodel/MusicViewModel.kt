package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TrackEntity
import com.example.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = TrackRepository(db.trackDao())

    val historyList: StateFlow<List<TrackEntity>> = repository.allTracks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val prompt = MutableStateFlow("")
    val selectedStyle = MutableStateFlow("EDM")
    val isGenerating = MutableStateFlow(false)
    val isGeneratedState = MutableStateFlow(false)
    val processingStep = MutableStateFlow(0) // 0: Idle, 1: Lyrics, 2: Synth, 3: Mixing
    
    val currentTitle = MutableStateFlow("තවම සින්දුවක් සාදා නැත")
    val currentLyrics = MutableStateFlow("")
    val isPlaying = MutableStateFlow(false)
    val sliderValue = MutableStateFlow(0.0f)

    val errorMessage = MutableStateFlow<String?>(null)

    private var playbackJob: Job? = null

    // Fallback creative lyrics database to use when offline or when the API key is not configured
    private val localLyricsTemplates = listOf(
        mapOf(
            "title" to "සිහින යාත්‍රා (AI EDM Mix)",
            "lyrics" to """
                [Verse 1]
                ඈත අහසේ තරු දිලිසෙයි...
                මගේ සිහින ඔබ ලඟ නැවතෙයි...
                පාළු සීතල රෑ පුරා...
                ඔබේ මතකය හද පිරෙයි...

                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                නියොන් එළියෙන් නැහැවුණු...
                මේ රැයේ මා තනි නොවේ...

                [Bridge]
                කාලය ගෙවී යයි හෙමින් සෙමින්...
                අලුත් බලාපොරොත්තුවක් අරන්...
                සංසාරේ මග දිගේ අපි යක්...
                නොකා නොබී සතුටෙන් ඉමු දැන්...
                
                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                නියොන් එළියෙන් නැහැවුණු...
                මේ රැයේ මා තනි නොවේ...
            """.trimIndent()
        ),
        mapOf(
            "title" to "මතක මාවත (AI LoFi Chill)",
            "lyrics" to """
                [Verse 1]
                වහින වැස්සේ තෙමුණු මතක...
                පාර දිගේ පාවී යනවා...
                නොකී රහසක් හිතේ තියන්...
                සඳත් අද නම් හැඬවෙනවා...

                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                කවදා හෝ අපි හමුවෙනා...
                ඒ සිහිනය මට දැනෙනවා...
                
                [Verse 2]
                හිත් කොනක තනිවුණු කතාවක්...
                සුළඟට මුසු වී කොඳුරනවා...
                නෑසෙන සේ මගේ කඳුලැලි...
                අහසෙන් වැස්සක් වෙනවා...

                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                කවදා හෝ අපි හමුවෙනා...
                ඒ සිහිනය මට දැනෙනවා...
            """.trimIndent()
        ),
        mapOf(
            "title" to "නවතින්නෙ නෑ (AI Trap Rap)",
            "lyrics" to """
                [Verse 1]
                කවුරු මොනවා කිව්වත් එකයි...
                යන පාර මට හොඳටම විශ්වාසයි...
                වැටුණු තැනින් නැගිටලා මං...
                දිනන දවස වැඩි ඈතක නෙවෙයි...

                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                ගහන ගැම්මට පස්ස බලන්නෑ...
                ජයග්‍රහණය මගේම වේ...

                [Verse 2]
                කඳු තරණය කරලා අපි ආවෙ...
                කටු අකුල් මතින් මග හැදුවේ...
                කිසිවෙකුටත් බෑ මාව වට්ටන්න...
                මගේ ශක්තිය හදවතෙයි තිබ්බේ...

                [Chorus]
                නවතින්නෑ මා ජීවිතේ...
                සිතේ ගීතේ ඔබම වේ...
                ගහන ගැම්මට පස්ස බලන්නෑ...
                ජයග්‍රහණය මගේම වේ...
            """.trimIndent()
        ),
        mapOf(
            "title" to "සඳ රෑ සිහිනය (AI Sad Melodic)",
            "lyrics" to """
                [Verse 1]
                නොකියාම නික්මී ගිය දා...
                ලෝකයම නිහඬයි වගේ දැනුණා...
                ඔබ දිනූ සතුට මට පෙනෙනා...
                මගේ හිත තනියෙන් වැලපුණා...

                [Chorus]
                අමතක කරන්නට බෑ කිසිදා...
                ඔබ මට දුන් ආදර කතා...
                සඳ නැති කලුවර අහසේ...
                තනිවෙමි මා මේ රෑ පුරා...

                [Bridge]
                කඳුලින් ලියූ මේ ගීතිකාව...
                ඔබට ඇසේවා සුළඟේ පාවී...
                
                [Chorus]
                අමතක කරන්නට බෑ කිසිදා...
                ඔබ මට දුන් ආදර කතා...
                සඳ නැති කලුවර අහසේ...
                තනිවෙමි මා මේ රෑ පුරා...
            """.trimIndent()
        )
    )

    fun setPrompt(text: String) {
        prompt.value = text
    }

    fun setStyle(style: String) {
        selectedStyle.value = style
    }

    fun togglePlayPause() {
        isPlaying.value = !isPlaying.value
        if (isPlaying.value) {
            startPlayProgress()
        } else {
            playbackJob?.cancel()
        }
    }

    fun seekForward() {
        val currentVal = sliderValue.value
        // Seek 10s forward of 45s (approx 10/45)
        val step = 10f / 45f
        sliderValue.value = (currentVal + step).coerceAtMost(1.0f)
    }

    fun seekBackward() {
        val currentVal = sliderValue.value
        // Seek 10s backwards of 45s (approx 10/45)
        val step = 10f / 45f
        sliderValue.value = (currentVal - step).coerceAtLeast(0.0f)
    }

    fun updateSlider(value: Float) {
        sliderValue.value = value
    }

    private fun startPlayProgress() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (isPlaying.value && sliderValue.value < 1.0f) {
                delay(100)
                // Total duration is 45s, so we add (0.1/45) to slider every 100ms
                val newVal = sliderValue.value + (0.1f / 45.0f)
                if (newVal >= 1.0f) {
                    sliderValue.value = 1.0f
                    isPlaying.value = false
                } else {
                    sliderValue.value = newVal
                }
            }
        }
    }

    fun deleteTrack(id: Int) {
        viewModelScope.launch {
            repository.deleteTrackById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun selectTrackFromHistory(track: TrackEntity) {
        playbackJob?.cancel()
        isPlaying.value = false
        currentTitle.value = track.title
        currentLyrics.value = track.lyrics
        isGeneratedState.value = true
        sliderValue.value = 0.0f
        selectedStyle.value = track.style
        prompt.value = track.prompt
        errorMessage.value = null
    }

    fun startGenerating() {
        val currentPrompt = prompt.value.trim()
        val currentStyle = selectedStyle.value

        if (currentPrompt.isEmpty()) return

        viewModelScope.launch {
            isGenerating.value = true
            isGeneratedState.value = false
            isPlaying.value = false
            sliderValue.value = 0.0f
            errorMessage.value = null

            // Step 1: Processing lyrics
            processingStep.value = 1
            delay(1500)

            // Step 2: Synthesis
            processingStep.value = 2
            delay(1500)

            // Step 3: Mixing and completion
            processingStep.value = 3
            delay(1500)

            val apiKey = try {
                com.example.BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                ""
            }

            var generatedLyrics = ""
            var finalTitle = ""

            // Elegant fallback if API Key is a placeholder, blank or invalid
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                val randomIndex = Random.nextInt(localLyricsTemplates.size)
                val template = localLyricsTemplates[randomIndex]
                
                finalTitle = "${currentPrompt.take(20)}... ($currentStyle AI Mix)"
                generatedLyrics = template["lyrics"] ?: ""
            } else {
                try {
                    generatedLyrics = repository.generateTrackLyrics(currentPrompt, currentStyle, apiKey)
                    
                    // Parse title or let the model decide. Let's make a beautiful title based on prompt
                    val cleanedLines = generatedLyrics.lines().map { it.trim() }
                    val possibleTitle = cleanedLines.firstOrNull { it.isNotEmpty() && !it.startsWith("[") }
                    
                    finalTitle = if (possibleTitle != null && possibleTitle.length < 50) {
                        possibleTitle
                    } else {
                        "${currentPrompt.take(20).trim()}... ($currentStyle AI Mix)"
                    }
                } catch (e: Exception) {
                    // Fail gracefully and use fallback template to keep user friction 0
                    errorMessage.value = "Gemini API could not connect: ${e.message}. Using creative templates..."
                    val template = localLyricsTemplates[Random.nextInt(localLyricsTemplates.size)]
                    finalTitle = "${currentPrompt.take(20)}... ($currentStyle AI Mix)"
                    generatedLyrics = "[දැනුම්දීම: Gemini API යතුර සක්‍රීය නැත, එබැවින් මෙම පද සැකිල්ල ක්‍රියාත්මක වේ]\n\n" + (template["lyrics"] ?: "")
                }
            }

            currentTitle.value = finalTitle
            currentLyrics.value = generatedLyrics
            isGeneratedState.value = true
            isGenerating.value = false
            processingStep.value = 0

            // Save to Local history persistence
            repository.insertTrack(
                TrackEntity(
                    title = finalTitle,
                    prompt = currentPrompt,
                    style = currentStyle,
                    lyrics = generatedLyrics
                )
            )
        }
    }
}
