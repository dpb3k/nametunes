package com.example.namethattune

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ResultsScreen(score: Int, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4B43))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Game Over! Your score: $score",
            fontFamily = PressStart2P,
            fontSize = 16.sp,
            color = Color.White
        )

        // Restart Button
        Button(
            onClick = {
                // Navigate back to the game screen and reset the back stack
                navController.navigate("gameScreen") {
                    popUpTo("gameScreen") { inclusive = true } // This will reset the back stack
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Restart Game", fontFamily = PressStart2P, fontSize = 12.sp, color = Color.Black)
        }

        // Go back to home button (optional)
        Button(
            onClick = {
                navController.navigate("homeScreen") // Replace with actual home screen if necessary
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Back to Home", fontFamily = PressStart2P, fontSize = 12.sp, color = Color.Black)
        }
    }
}
