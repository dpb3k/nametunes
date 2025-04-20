package com.example.namethattune.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EqualizerVisualizer(
    barCount: Int = 7,
    barWidth: Dp = 6.dp,
    barMaxHeight: Dp = 60.dp,
    barColor: Color = Color.Red
) {
    val barHeights = remember {
        List(barCount) { Animatable((10..barMaxHeight.value.toInt()).random().toFloat()) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            // Launch animations for all bars at once
            barHeights.forEach { bar ->
                launch {
                    val target = (10..barMaxHeight.value.toInt()).random().toFloat()
                    bar.animateTo(
                        target,
                        animationSpec = tween(durationMillis = 200)
                    )
                }
            }
            delay(400) // control overall speed here
        }
    }

    Row(
        modifier = Modifier
            .height(barMaxHeight)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        barHeights.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(anim.value.dp)
                    .background(barColor, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}

