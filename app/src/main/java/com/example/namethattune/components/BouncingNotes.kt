package com.example.namethattune.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.namethattune.R

@Composable
fun BouncingNote() {
    // Screen width for constraint
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Define the animation for the note's X position
    val transition = rememberInfiniteTransition()
    val xOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = screenWidth.value - 100f,  // Ensure the note stops at the edge of the screen
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000, // Slow down the speed (4 seconds)
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse // Reverse direction when reaching end
        )
    )

    // Define the animation for the note's Y position (bounce effect vertically)
    val yOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,  // Change this for more or less vertical movement
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000, // Slow down the vertical bounce as well
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Use the music note image instead of a circle
    Box(
        modifier = Modifier
            .offset(x = xOffset.value.dp, y = yOffset.value.dp) // Move the note based on X and Y offsets
            .padding(16.dp)
            .size(80.dp) // Adjust the size of the note (medium size)
    ) {
        Image(
            painter = painterResource(id = R.drawable.musical_note), // Use your musical note image here
            contentDescription = "Bouncing Musical Note",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SingleBouncingNote() {
    // Add just one note to the screen
    BouncingNote()
}
