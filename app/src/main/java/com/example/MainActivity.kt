package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GlassBase
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlowPink
import com.example.ui.theme.GlowPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MusicViewModel = viewModel()) {
    val promptValue by viewModel.prompt.collectAsState()
    val selectedStyleValue by viewModel.selectedStyle.collectAsState()
    val isGeneratingValue by viewModel.isGenerating.collectAsState()
    val isGeneratedStateValue by viewModel.isGeneratedState.collectAsState()
    val processingStepValue by viewModel.processingStep.collectAsState()
    
    val currentTitleValue by viewModel.currentTitle.collectAsState()
    val currentLyricsValue by viewModel.currentLyrics.collectAsState()
    val isPlayingValue by viewModel.isPlaying.collectAsState()
    val sliderValueVal by viewModel.sliderValue.collectAsState()
    val errorValue by viewModel.errorMessage.collectAsState()

    val historyListVal by viewModel.historyList.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(end = 48.dp) // Offset action button to center perfectly
                    ) {
                        Text(
                            text = "Sinhala AI Music Creator",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Sinhala Music AI සක්‍රීයයි! ✨", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("action_magic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Power Mode Indicator",
                            tint = GlowPink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF110724),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkBg,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. INPUT PROMPT BLOCK
            GlassContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_container_card")
            ) {
                Text(
                    text = "ඔයාගේ සින්දුවේ අදහස (Prompt) මෙතන ලියන්න:",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                OutlinedTextField(
                    value = promptValue,
                    onValueChange = { if (it.length <= 150) viewModel.setPrompt(it) },
                    placeholder = {
                        Text(
                            text = "උදා: 'ජීවිතේ දුක ජයගන්න වේගවත් EDM style Sinhala song එකක්...'",
                            color = Color.White.copy(alpha = 0.35f),
                            fontSize = 14.sp
                        )
                    },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("prompt_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1D1238),
                        unfocusedContainerColor = Color(0xFF1D1238),
                        focusedBorderColor = GlowPurple,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${promptValue.length}/150",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. STYLE SELECTION ROW
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "සංගීත රටාව තෝරන්න (Style):",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val musicStyles = listOf("EDM", "Sad", "Rap", "Trap", "LoFi", "Hindi Remix", "Chill")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("style_chips_row")
                ) {
                    items(musicStyles) { style ->
                        val isSelected = style == selectedStyleValue
                        val chipBg by animateColorAsState(if (isSelected) GlowPurple else GlassBase)
                        val chipBorder = if (isSelected) GlowPink else GlassBorder

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                                .clickable { viewModel.setStyle(style) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("style_chip_$style")
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ERROR DISPLAY CONTAINER (IF ANY)
            errorValue?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(Color(0x33FF0055), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFFF0055), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚠️ $err",
                        color = Color(0xFFFF88AA),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. AI GENERATE BUTTON
            if (isGeneratingValue) {
                LoadingStateCard(step = processingStepValue)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GlowPurple, GlowPink)
                            )
                        )
                        .clickable(enabled = promptValue.trim().isNotEmpty()) {
                            viewModel.startGenerating()
                        }
                        .padding(vertical = 14.dp)
                        .testTag("generate_music_button")
                        .shadow(if (promptValue.trim().isNotEmpty()) 15.dp else 0.dp, ambientColor = GlowPink, spotColor = GlowPink),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "magic creation",
                            tint = if (promptValue.trim().isNotEmpty()) Color.White else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI සින්දුව නිර්මාණය කරන්න (Generate)",
                            color = if (promptValue.trim().isNotEmpty()) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // 3 & 4. GENERATED PLAY MODULE (VISIBLE WHEN READY)
            AnimatedVisibility(
                visible = isGeneratedStateValue,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 6. ALBUM COVER PREVIEW ART
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF311B92), Color(0xFF00838F))
                                )
                            )
                            .border(2.dp, GlowPink.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .shadow(25.dp, shape = RoundedCornerShape(20.dp), ambientColor = GlowPink, spotColor = GlowPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "note",
                                tint = GlowPink,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "STUDIO ALBUM ART",
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. MUSIC PLAYER DESIGN SYSTEM
                    GlassContainer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audio_player_visualizer")
                    ) {
                        Text(
                            text = currentTitleValue,
                            color = GlowPink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // ANIMATING WAVEFORM CANVAS
                        WaveformVisualizer(
                            isPlaying = isPlayingValue,
                            modifier = Modifier.align(Alignment.CenterHorizontally).testTag("waveform_waves")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // PLAYBACK PROGRESS SLIDER
                        Slider(
                            value = sliderValueVal,
                            onValueChange = { viewModel.updateSlider(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = GlowPink,
                                activeTrackColor = GlowPink,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("player_progress_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val elapsedSeconds = (sliderValueVal * 45).toInt()
                            Text(
                                text = "0:${elapsedSeconds.toString().padStart(2, '0')}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "0:45",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // CONTROL COMMAND BAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.seekBackward() },
                                modifier = Modifier.testTag("rewind_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay10,
                                    contentDescription = "rewind 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(GlowPurple)
                                    .clickable { viewModel.togglePlayPause() }
                                    .testTag("playback_toggle_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlayingValue) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "play-pause-toggle",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(20.dp))
                            IconButton(
                                onClick = { viewModel.seekForward() },
                                modifier = Modifier.testTag("forward_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forward10,
                                    contentDescription = "forward 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. GENERATED LYRICS CONTAINER
                    GlassContainer(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📄 AI නිපදවූ පද මාලාව:",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentLyricsValue))
                                        Toast.makeText(context, "පද මාලාව Copy කරගත්තා! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_lyrics_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "copy",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "සින්දුවේ පද බෙදාගැනීමට සූදානම්! 🔗", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "share",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentLyricsValue,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7. FILE EXPORTS BLOCK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1D1238))
                                .clickable {
                                    Toast.makeText(context, "📥 MP3 ගොනුව බාගත කිරීම සාර්ථකයි!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "download mp3",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Download MP3",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1D1238))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(currentLyricsValue))
                                    Toast.makeText(context, "💾 සින්දුවේ පද මාලාව සුරැකිණ!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "save lyrics",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Save Lyrics",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 6. PERSISTENT HISTORIC CREATIONS DB LAYER
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "history",
                            tint = GlowPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "මගේ නිර්මාණ එකතුව (Creations)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (historyListVal.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.clearAllHistory()
                                Toast.makeText(context, "සියලුම නිර්මාණ මකා දැමුවා! 🧹", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "clear history",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (historyListVal.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GlassBase.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, GlassBorder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "තවමත් කිසිදු සින්දුවක් සාදා නොමැත.\nඉහත කොටසෙන් සින්දුවක් සාදා සුරකින්න! \uD83C\uDFBC",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        historyListVal.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GlassBase.copy(alpha = 0.6f))
                                    .border(1.dp, GlassBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.selectTrackFromHistory(track)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("track_history_item_${track.id}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "music symbol",
                                        tint = GlowPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = track.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Style: ${track.style} | Concept: ${track.prompt}",
                                            color = Color.White.copy(alpha = 0.45f),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteTrack(track.id)
                                        Toast.makeText(context, "නිරමාණ ගොනුව මකා දැමුණා!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("delete_track_${track.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "delete",
                                        tint = Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassBase.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun LoadingStateCard(step: Int) {
    val stepText = when (step) {
        1 -> "පද මාලාව සකසමින් පවතියි..."
        2 -> "සංගීත ඛණ්ඩය නිර්මාණය වෙමින් පවතියි..."
        3 -> "කටහඬ සහ ශබ්ද සංකලනය වෙමින් පවතියි..."
        else -> "AI සැකසුම් ක්‍රියාත්මක වේ..."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassBase.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
            .border(1.dp, GlowPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(24.dp)
            .testTag("generation_loading_card"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = GlowPink,
            strokeWidth = 3.dp,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stepText,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}
