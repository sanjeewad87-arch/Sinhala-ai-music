package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    activeColor: Color = Color(0xFFFF007F), // Neon Pink
    secondaryColor: Color = Color(0xFFD500F9), // Neon Purple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier
            .width(180.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val durations = listOf(420, 560, 310, 480, 620, 380, 510, 440, 590, 350, 490, 610, 320, 460, 530, 400, 570, 330)
        
        for (i in 0 until barCount.coerceAtMost(durations.size)) {
            val baseDuration = durations[i]
            
            // Create independent bouncing factor if playing, else return static low bar
            val factor by if (isPlaying) {
                infiniteTransition.animateFloat(
                    initialValue = 0.15f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(baseDuration, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_$i"
                )
            } else {
                androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0.12f) }
            }

            val barHeight = 4.dp + (40.dp * factor)

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                activeColor,
                                secondaryColor
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            if (i < barCount - 1) {
                Box(modifier = Modifier.width(3.dp))
            }
        }
    }
}
