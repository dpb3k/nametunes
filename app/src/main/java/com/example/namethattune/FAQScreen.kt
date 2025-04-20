package com.example.namethattune

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namethattune.components.FaqItem

data class FaqEntry(
    val question: String,
    val answer: String
)

@Composable
fun FAQScreen() {
    val faqList = listOf(
        FaqEntry("How do I play?", "You will hear a short music clip and must choose the correct song title from the options."),
        FaqEntry("Where does the music play from?", "Songs are pulled from a curated library of licensed audio clips."),
        FaqEntry("What are the different game modes?", "Try solo mode or challenge friends in multiplayer."),
        FaqEntry("Can I skip a song?", "Yes, but skipping reduces your final score."),
        FaqEntry("What information does the game collect about me?", "Only minimal gameplay data is collected to improve the experience.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FAQs",
            fontSize = 20.sp,
            fontFamily = PressStart2P,
            color = Color(0xFF00332D),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            faqList.forEach { entry ->
                FaqItem(entry)
            }
        }
    }
}

