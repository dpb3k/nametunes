package com.example.namethattune.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.namethattune.FaqEntry
import com.example.namethattune.PressStart2P
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun FaqItem(entry: FaqEntry) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B4B43), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF00FF99),
                modifier = Modifier
                    .size(20.dp)
                    .rotate(if (expanded) 90f else 0f) // rotates right/down
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = entry.question,
                fontSize = 14.sp,
                fontFamily = PressStart2P,
                color = Color.White
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = entry.answer,
                fontSize = 12.sp,
                fontFamily = PressStart2P,
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}