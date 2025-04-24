package com.example.namethattune.components
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.zIndex
import com.example.namethattune.PressStart2P
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.namethattune.PressStart2P
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun GenreDropdown(selectedGenre: String, onGenreSelected: (String) -> Unit) {
    val genreMap = mapOf(
        "Hip-Hop" to "hip-hop",
        "Country" to "country",
        "R&B" to "r%26b",
        "Pop" to "pop",
        "Latin" to "latin",
        "Rock" to "rock",
        "Dance" to "dance",  // Simplify 'Dance/Electronic' to 'dance'
        "Indie" to "indie",
        "Christian/Gospel" to "christian"  // Simplify 'Christian/Gospel' to 'christian'
    )

    val genres = genreMap.keys.toList()
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedGenre) }

    Box(
        modifier = Modifier
            .fillMaxWidth() // Ensure it takes up full width
            .padding(8.dp) // Optional: Add padding for aesthetics
    ) {
        // This TextField is clickable and will trigger the dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth() // Make sure the TextField takes full width
                .background(Color(0xFF1B4B43)) // Background color of the dropdown field
                .clickable { expanded = true } // Make the field clickable
                .padding(12.dp) // Padding for the text inside
        ) {
            Text(
                text = if (selectedGenre.isEmpty()) "Select Genre" else selectedGenre,
                fontFamily = PressStart2P,
                fontSize = 12.sp,
                color = Color.White
            )
            // Arrow icon for dropdown indication
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown Icon",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd) // Positioning the dropdown arrow at the top right
                    .padding(8.dp)
            )
        }

        // Dropdown Menu for Genre Selection
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth() // Ensure dropdown fills the width
                .background(Color(0xFF1B4B43)) // Match background color
                .padding(0.dp)
        ) {
            genres.forEach { genreDisplay ->
                DropdownMenuItem(
                    text = {
                        Text(
                            genreDisplay,
                            fontFamily = PressStart2P,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        textFieldValue = genreDisplay // Set selected genre to text field
                        onGenreSelected(genreMap[genreDisplay]!!) // Update genre
                        expanded = false // Close dropdown on selection
                    }
                )
            }
        }
    }
}
