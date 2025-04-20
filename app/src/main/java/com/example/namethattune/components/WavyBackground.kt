package com.example.namethattune.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.sin


@Composable
fun AnimatedWaveBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val waveHeight = 40f

        // Fill background first with diagonal gradient
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF001F1F), // dark green

                    Color(0xFF00FF99)  // alien synth green
                ),
                start = Offset.Zero,
                end = Offset(width, height)
            ),
            size = size
        )

        // Create and draw animated wave path
        val path = Path().apply {
            moveTo(0f, height / 2)

            for (x in 0..width.toInt() step 1) {
                val y = (waveHeight * sin((x.toFloat() / width) * 2 * Math.PI + phase)).toFloat()
                lineTo(x.toFloat(), height / 2 + y)
            }

            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF001F1F),
                    Color(0xFF004D40),
                    Color(0xFF00C78C),
                    Color(0xFF00FF99)
                ),
                start = Offset.Zero,
                end = Offset(width, height)
            )
        )
    }
}


