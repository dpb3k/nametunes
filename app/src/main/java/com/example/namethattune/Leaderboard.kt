package com.example.namethattune

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import io.ktor.http.HttpMethod
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
//import androidx.compose.material3.start
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import androidx.core.content.edit
import kotlinx.serialization.encodeToString

@Serializable
data class LeaderboardEntry(
    val rank : Int,
    val play_name: String,
    val genre: String,
    val score: Int
)

suspend fun fetchLeaderboard(): List<LeaderboardEntry> {
    val url = "https://nametunes.onrender.com/api/getLeaderboard" // Update with the correct URL
    val response: HttpResponse = client.get(url)
    val leaderboard: List<LeaderboardEntry> = Json { ignoreUnknownKeys = true }
        .decodeFromString(response.bodyAsText())  // Deserialize the response while ignoring unknown keys
    return leaderboard
}

fun cacheLeaderboard(context: Context, leaderboard: List<LeaderboardEntry>) {
    val sharedPrefs = context.getSharedPreferences("LeaderboardCache", Context.MODE_PRIVATE)
    // The type is inferred automatically here because of the @Serializable annotation on LeaderboardEntry
    val jsonString = Json.encodeToString(leaderboard) // No need to specify type explicitly
    sharedPrefs.edit().putString("leaderboard", jsonString).apply() // Save to SharedPreferences
}


fun loadLeaderboardCache(context: Context): List<LeaderboardEntry> {
    val sharedPrefs = context.getSharedPreferences("LeaderboardCache", Context.MODE_PRIVATE)
    val jsonString = sharedPrefs.getString("leaderboard", null)
    return if (jsonString != null) {
        Json.decodeFromString(jsonString) // Convert JSON string back to list
    } else {
        emptyList() // Return empty list if no cache is found
    }
}


suspend fun submitScore(playerName: String, genre: String, score: Int) {
    try {
        val url = "https://nametunes.onrender.com/api/submitScore" // Make sure this is the correct URL for your backend
        client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                """{
                    "play_name": "$playerName",
                    "genre": "$genre",
                    "score": $score
                }"""
            )
        }
    } catch (e: Exception) {
        Log.e("SubmitScore", "Error submitting score: ${e.message}")
    }
}

@Composable
fun LeaderboardScreen() {
    var mediaPlayer: MediaPlayer? = null
    val context = LocalContext.current // Get the current context
    val leaderboardEntries = remember { mutableStateListOf<LeaderboardEntry>() } // Use remember to persist data
    var isLoading by remember { mutableStateOf(true) }

    // Start the theme song when the leaderboard screen is displayed
    LaunchedEffect(Unit) {
        mediaPlayer = MediaPlayer.create(context, R.raw.game_theme1) // Load the theme song
        mediaPlayer?.start() // Start the music

        // Optional: Set looping if you want it to keep playing in a loop
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.1f, 0.1f) // Values range from 0.0f to 1.0f for each channel
    }

    // Stop the theme song when navigating away from the screen (cleanup)
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop() // Stop the music
            mediaPlayer?.release() // Release resources
        }
    }

    // Use LaunchedEffect to only fetch the data once
    LaunchedEffect(Unit) {
        if (leaderboardEntries.isEmpty()) { // Fetch data only if the list is empty
            try {
                // Check if cached data is available
                val cachedLeaderboard = loadLeaderboardCache(context)
                if (cachedLeaderboard.isNotEmpty()) {
                    leaderboardEntries.addAll(cachedLeaderboard)
                    isLoading = false
                } else {
                    // Fetch data from API if no cached data
                    leaderboardEntries.clear()
                    leaderboardEntries.addAll(fetchLeaderboard()) // Fetch leaderboard data from API
                    cacheLeaderboard(context, leaderboardEntries) // Cache the fetched data
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("LeaderboardScreen", "Error fetching leaderboard: ${e.message}")
            }
        } else {
            isLoading = false // If data is already loaded, stop the loading spinner
        }
    }

    // Set the background image and make it fit the screen
    Box(
        modifier = Modifier
            .fillMaxSize() // Make the background fill the whole screen
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.leaderboard_bg), // Your drawable resource
            contentDescription = "Leaderboard background",
            modifier = Modifier
                .fillMaxSize()  // Ensures the background image covers the full screen size
                .align(Alignment.Center), // Centers the background image
            contentScale = ContentScale.Crop // This ensures the image scales to fill the screen without distortion
        )

        // The leaderboard content is placed over the background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Leaderboard",
                fontFamily = PressStart2P,
                fontSize = 20.sp,
                color = Color(0xFF9B59B6),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LeaderboardHeader() // The header for the leaderboard

            Spacer(modifier = Modifier.height(12.dp)) // Add space between the header and leaderboard rows

            // Show loading indicator while data is loading
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp) // Ensure there's padding at the bottom of the list
                ) {
                    items(leaderboardEntries) { entry ->
                        LeaderboardRow(entry = entry)
                        Spacer(modifier = Modifier.height(8.dp)) // Add spacing between each entry
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardHeader() {
    val columnWidths = listOf(60.dp, 80.dp, 60.dp, 80.dp)

    Row(
        modifier = Modifier
            .background(Color(0xFF1B4B43), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp) // Added padding for header
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val headers = listOf("RANK", "PLAYER NAME", "SCORE", "GENRE")
        headers.forEachIndexed { index, title ->
            Box(
                modifier = Modifier.width(columnWidths[index])
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontFamily = PressStart2P,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    val columnWidths = listOf(60.dp, 80.dp, 60.dp, 80.dp)

    Row(
        modifier = Modifier
            .background(Color(0xFF1B4B43), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val values = listOf(
            entry.rank.toString(),
            entry.play_name,
            entry.score.toString(),
            entry.genre
        )

        values.forEachIndexed { index, value ->
            Box(
                modifier = Modifier.width(columnWidths[index])
            ) {
                Text(
                    text = value,
                    fontSize = 10.sp,
                    fontFamily = PressStart2P,
                    color = Color.White,
                    modifier = if (index == 3) Modifier.padding(start = 4.dp) else Modifier
                )
            }
        }
    }
}



