package com.example.namethattune.components
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.namethattune.PressStart2P

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

    Box {
        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            label = {
                Text(
                    "Genre",
                    fontFamily = PressStart2P,
                    fontSize = 12.sp,
                    color = Color.White
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    tint = Color.White
                )
            },
            textStyle = TextStyle(
                fontFamily = PressStart2P,
                fontSize = 12.sp,
                color = Color.White
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White),
            properties = PopupProperties(focusable = true)
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
                        Log.d("GenreDropdown", "Selected genre: ${genreMap[genreDisplay]}")
                        onGenreSelected(genreMap[genreDisplay]!!) // ✅ Send the normalized key!
                        expanded = false
                    }
                )
            }
        }
    }
}
