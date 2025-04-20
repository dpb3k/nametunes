package com.example.namethattune

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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

@Serializable
data class LeaderboardEntry(
    val rank : Int,
    val play_name: String,
    val genre: String,
    val score: Int
)

suspend fun fetchLeaderboard(): List<LeaderboardEntry> {
    val url = "https://namethattune-2fcad176bcaa.herokuapp.com/api/getLeaderboard" // Update with the correct URL
    val response: HttpResponse = client.get(url)
    val leaderboard: List<LeaderboardEntry> = Json { ignoreUnknownKeys = true }
        .decodeFromString(response.bodyAsText())  // Deserialize the response while ignoring unknown keys
    return leaderboard
}


suspend fun submitScore(playerName: String, genre: String, score: Int) {
    try {
        val url = "https://namethattune-2fcad176bcaa.herokuapp.com/api/submitScore" // Make sure this is the correct URL for your backend
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
    val leaderboardEntries by remember { mutableStateOf(mutableListOf<LeaderboardEntry>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        try {
            leaderboardEntries.clear()
            leaderboardEntries.addAll(fetchLeaderboard()) // Fetch leaderboard data
        } catch (e: Exception) {
            Log.e("LeaderboardScreen", "Error fetching leaderboard: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Add padding and make the column scrollable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp) // Add overall padding for the whole screen
    ) {
        Text(
            text = "Leaderboard",
            fontFamily = PressStart2P,
            fontSize = 20.sp,
            color = Color(0xFF00332D),
            modifier = Modifier.padding(bottom = 16.dp) // Padding for the title
        )

        LeaderboardHeader() // The header for the leaderboard

        // Padding between the header and leaderboard rows
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
                }
            }
        }
    }
}

@Composable
fun LeaderboardHeader() {
    val columnWidths = listOf(60.dp, 120.dp, 60.dp, 80.dp)

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
    val columnWidths = listOf(60.dp, 120.dp, 60.dp, 80.dp)

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
                    color = Color.White
                )
            }
        }
    }
}



