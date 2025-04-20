package com.example.namethattune.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import com.example.namethattune.PressStart2P
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun AudioPlayer(previewUrl: String?) {
    var isPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Initialize ExoPlayer only once, even when the track changes
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // Update the ExoPlayer with the new track whenever previewUrl changes
    LaunchedEffect(previewUrl) {
        previewUrl?.let {
            val mediaItem = MediaItem.Builder()
                .setUri(it) // Use setUri to set the URI from the preview URL
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    // Lifecycle management to release ExoPlayer when the screen is destroyed
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Observe the lifecycle and release the ExoPlayer when the activity/fragment is destroyed
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                exoPlayer.release()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Start/Stop logic for the play/pause button
    val playPauseButtonText = if (isPlaying) "Pause" else "Play"

    Button(
        onClick = {
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            isPlaying = !isPlaying
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text(playPauseButtonText, fontFamily = PressStart2P, fontSize = 12.sp, color = Color.Black)
    }

    // Display ExoPlayer's UI
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxWidth().height(60.dp) // Adjust player size
    )
}
