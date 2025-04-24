package com.example.namethattune

import android.media.browse.MediaBrowser
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.namethattune.models.Track
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.platform.LocalDensity
import androidx.media3.common.Player
import com.example.namethattune.components.AudioPlayer
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.coroutines.delay
import kotlinx.coroutines.async
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite

@Composable
fun RealGameScreen(playerName: String, selectedGenre: String, navController: NavController) {
    var track by remember { mutableStateOf<Track?>(null) }
    var additionalTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var correctAnswerText by remember { mutableStateOf("") }
    var questionCount by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(3) } // Start with 3 lives
    var isAnswerButtonEnabled by remember { mutableStateOf(true) }  // Track if the answer buttons are enabled
    var progress by remember { mutableStateOf(0f) } // Progress for the loading bar
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedGenre) {
        coroutineScope.launch {
            try {
                val trackDeferred = async { fetchRandomTrack(selectedGenre) }
                val additionalTracksDeferred = async {
                    mutableListOf<Track>().apply {
                        repeat(3) { add(fetchRandomTrack(selectedGenre)) }
                    }
                }

                track = trackDeferred.await()
                additionalTracks = additionalTracksDeferred.await()

            } catch (e: Exception) {
                Log.e("RealGameScreen", "Error fetching track: ${e.message}")
            }
        }
    }

    // Handle answers
    fun checkAnswer() {
        // Check if the answer is correct
        if (selectedAnswer == track?.trackName) {
            score += 1
            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            // Handle incorrect answer
            Toast.makeText(context, "Incorrect. The correct answer was ${track?.trackName}", Toast.LENGTH_SHORT).show()
            correctAnswerText = "The song is: ${track?.trackName}"

            lives -= 1

            if (lives == 0) {
                isGameOver = true
                // Submit the score when the game is over (incorrect answer)
                coroutineScope.launch {
                    submitScore(playerName, selectedGenre, score)
                }
            }
        }

        // Increment the question count after answering
        questionCount += 1

        // If the game is over, we don't want to proceed to the next question
        if (isGameOver) return

        // Proceed to the next question after a brief delay
        coroutineScope.launch {
            var loadingTime = 1500
            var currentTime = 0
            while (currentTime < loadingTime) {
                delay(100) // Delay for 100ms
                currentTime += 100
                progress = (currentTime / loadingTime.toFloat()) // Update progress
            }
            correctAnswerText = "" // Clear the correct answer message
            track = fetchRandomTrack(selectedGenre) // Fetch next track asynchronously
            isAnswerButtonEnabled = true  // Re-enable the answer buttons for the next question
        }
    }


    // If the track is not loaded yet, show a loading indicator
    if (track == null || additionalTracks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1B4B43))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Loading Track...",
                fontFamily = PressStart2P,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1B4B43))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {

            // Display hearts in the top left corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                // Display 3 hearts, and change color based on lives remaining
                val heartColor = listOf(
                    if (lives >= 3) Color.Red else Color.Black,
                    if (lives >= 2) Color.Red else Color.Black,
                    if (lives >= 1) Color.Red else Color.Black
                )
                heartColor.forEach { color ->
                    Icon(
                        imageVector = Icons.Filled.Favorite, // Using heart icon
                        contentDescription = "Heart",
                        tint = color,
                        modifier = Modifier.padding(8.dp).size(24.dp)
                    )
                }
            }

            if (!isGameOver) {
                Text(
                    text = "Question ${questionCount + 1}",
                    fontFamily = PressStart2P,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = "Guess the song!",
                    fontFamily = PressStart2P,
                    fontSize = 16.sp,
                    color = Color.White
                )

                track?.let {
                    AudioPlayer(previewUrl = it.preview_url)
                }

                if (correctAnswerText.isNotEmpty()) {
                    Text(
                        text = correctAnswerText,
                        fontFamily = PressStart2P,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                val answerChoices = listOf(track?.trackName ?: "Unknown Song") + additionalTracks.map { it.trackName }
                val shuffledAnswers = answerChoices.shuffled()

                // Use LazyColumn for scrollable answers
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp) // Add some padding between the choices
                ) {
                    itemsIndexed(shuffledAnswers) { index, answer ->
                        Button(
                            onClick = {
                                selectedAnswer = answer
                                isAnswerButtonEnabled = false  // Disable buttons after an answer is selected
                                checkAnswer()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            enabled = isAnswerButtonEnabled  // Disable button if it's false
                        ) {
                            Text(answer, fontFamily = PressStart2P, fontSize = 12.sp, color = Color.Black)
                        }
                    }
                }

                if (progress < 1f) {
                    LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = Color.Red,
                    trackColor = Gray,
                    strokeCap = StrokeCap.Round,
                    )
                }
            }

            // Show score and game over message after 5 questions
            if (isGameOver) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Game Over!",
                        fontFamily = PressStart2P,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Your score: $score",
                        fontFamily = PressStart2P,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp) // Add some space between the two lines
                    )

                    // Restart Button to go back to the GameScreen
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            "Restart Game",
                            fontFamily = PressStart2P,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }

        }
    }
}

suspend fun fetchRandomTrack(genre: String): Track {
    val response: HttpResponse = client.get("https://nametunes.onrender.com/deezer/randomTrack?genre=$genre")
    return response.body()
}
