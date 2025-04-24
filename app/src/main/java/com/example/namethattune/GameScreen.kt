package com.example.namethattune

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.res.fontResource
import androidx.navigation.NavController
import com.example.namethattune.components.GenreDropdown
import com.example.namethattune.models.Track
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.media.MediaPlayer
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}


@Composable
fun GameScreen(navController: NavController) {
    var playerName by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Get the keyboard controller to hide the keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Validate player name and genre
    val isValidInput = playerName.isNotEmpty() && playerName.length <= 12 && selectedGenre.isNotEmpty()

    // Column to display the UI
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1B4B43))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            // Player name input field
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "PLAYER:",
                    fontFamily = PressStart2P,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))

                // Outlined TextField with keyboard actions
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    modifier = Modifier.width(200.dp),
                    textStyle = TextStyle(
                        fontFamily = PressStart2P,
                        fontSize = 12.sp,
                        color = Color.White
                    ),
                    label = { Text("", fontFamily = PressStart2P, fontSize = 12.sp, color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Set the action to "Done" when Enter is pressed
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Hide the keyboard when "Done" (Enter) is pressed
                            keyboardController?.hide()
                        }
                    )
                )
            }

            // 🎵 Genre dropdown with parameters
            GenreDropdown(selectedGenre = selectedGenre, onGenreSelected = { selectedGenre = it })

            Spacer(modifier = Modifier.height(16.dp)) // Add space between fields and button

            // Start button click logic
            Button(
                onClick = {
                    when {
                        playerName.length > 12 -> {
                            // Show a toast if name is longer than 12 characters
                            Toast.makeText(context, "Name must be less than 12 characters", Toast.LENGTH_SHORT).show()
                        }
                        selectedGenre.isEmpty() -> {
                            // Show a toast if no genre is selected
                            Toast.makeText(context, "Must enter a genre", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // URL encode the genre before passing it in the request
                            val encodedGenre = URLEncoder.encode(selectedGenre, StandardCharsets.UTF_8.toString())
                            // Navigate to RealGameScreen when validation passes
                            navController.navigate("realGameScreen/${playerName}/${encodedGenre}")
                        }
                    }
                },
                enabled = isValidInput, // Enable the button only if validation passes
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp) // Ensure the button is visible and has space
            ) {
                Text("START", fontFamily = PressStart2P, fontSize = 12.sp, color = Color.Black)
            }
        }
    }
}
