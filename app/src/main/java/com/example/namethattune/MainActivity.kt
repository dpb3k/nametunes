package com.example.namethattune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.namethattune.components.EqualizerVisualizer
import kotlinx.coroutines.launch
import com.example.namethattune.ui.theme.NameThatTuneTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.math.roundToInt
import android.media.MediaPlayer
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.example.namethattune.components.AnimatedWaveBackground
import com.example.namethattune.components.SoundManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.rememberScrollState  // Import for scroll state
import androidx.compose.foundation.ScrollState  // Import for scroll state
import androidx.compose.foundation.verticalScroll  // Import for vertical scrolling
import androidx.compose.ui.Alignment

val PressStart2P = FontFamily(Font(R.font.press_start_2p))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            NameThatTuneTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = true // for black icons on light background

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.White,
                        darkIcons = useDarkIcons
                    )
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBarHeight = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopNavBar(onNavigate = { route ->
                navController.navigate(route)
            }, onHeightMeasured = {height -> navBarHeight.intValue = height})
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreenContent(navBarHeight = navBarHeight.intValue,
                                                        navController=navController) }
            composable("faq") { FAQScreen() }
            composable("game") {GameScreen(navController)}
            composable("realGameScreen/{playerName}/{selectedGenre}") { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                val selectedGenre = backStackEntry.arguments?.getString("selectedGenre") ?: ""
                RealGameScreen(playerName, selectedGenre, navController)
            }
            composable("resultsScreen/{score}") { backStackEntry ->
                val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
                ResultsScreen(score = score, navController = navController)
            }
            composable("leaderboard") { LeaderboardScreen() }
        }
    }
}

@Composable
fun HomeScreenContent(navBarHeight : Int, navController: NavController) {
    val density = LocalDensity.current
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? = null
    val extraPaddingPx = with(density) { (40.dp).toPx().toInt() }
    val scrollState = rememberScrollState()
    val rulesOffset = remember { mutableStateOf(0) }
    val welcomeHeight = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val screenHeightPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx().toInt()
    }
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val statusBarHeightPx = with(LocalDensity.current) { statusBarHeight.toPx().toInt() }
    val totalNavHeight = navBarHeight + statusBarHeightPx + extraPaddingPx

    // Trigger to scroll to top
    val onScrollToTop: () -> Unit = {
        coroutineScope.launch {
            scrollState.animateScrollTo(0) // Scroll to top in a coroutine
        }
    }

    // Start the theme song when the home screen is displayed
    LaunchedEffect(Unit) {
        mediaPlayer = MediaPlayer.create(context, R.raw.intro_bounce) // Load the theme song
        mediaPlayer?.start() // Start the music

        // Optional: Set looping if you want it to keep playing in a loop
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.8f, 0.8f) // Values range from 0.0f to 1.0f for each channel

    }

    // Stop the theme song when navigating away from the screen (cleanup)
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop() // Stop the music
            mediaPlayer?.release() // Release resources
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedWaveBackground(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            WelcomeSection(
                onRulesClick = {
                    coroutineScope.launch {
                        val currentScroll = scrollState.value
                        val targetPosition = (currentScroll + rulesOffset.value - totalNavHeight).coerceAtLeast(0)
                        scrollState.animateScrollTo(targetPosition)
                    }
                },
                onPlayClick = {
                    navController.navigate("game") // ✅ this does the navigation!
                },
                onHeightCalculated = {
                    welcomeHeight.value = it
                }
            )
            val dynamicSpacerHeight = (screenHeightPx - welcomeHeight.value).coerceAtLeast(0)
            Spacer(modifier = Modifier.height(with(LocalDensity.current) { dynamicSpacerHeight.toDp() }))
            RulesSection(
                onSectionPositioned = { yPosition -> rulesOffset.value = yPosition/* Store the Y position of rules section */ },
                scrollState = scrollState, // Pass scroll state here
                onScrollToTop = onScrollToTop // Pass scroll function to RulesSection
            )
            Footer()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@Composable
fun TopNavBar(onNavigate: (String) -> Unit,
              onHeightMeasured: (Int) -> Unit ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding() // adds status + nav bar padding
            .padding(vertical = 20.dp)
            .onGloballyPositioned { coordinates ->
                onHeightMeasured(coordinates.size.height)
            },
        horizontalArrangement = Arrangement.Center, // ⬅️ center items
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Home", "Game", "Leaderboard", "FAQs").forEach { label ->
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E676),
                fontFamily = PressStart2P,
                modifier = Modifier
                    .clickable {
                        when (label) {
                            "Home" -> onNavigate("home")
                            "Game" -> onNavigate("game")
                            "Leaderboard" -> onNavigate("leaderboard")
                            "FAQs" -> onNavigate("faq")
                        }
                    }
                    .padding(horizontal = 12.dp)
            )
        }
    }
}



@Composable
fun WelcomeSection(onRulesClick: () -> Unit, onPlayClick: () -> Unit, onHeightCalculated: (Int) -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    val infiniteTransition = rememberInfiniteTransition()
    // Define a floating value for animating the shadow (glow effect)
    val shadowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Shaking effect for the Play button
    val shakeAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Define a glowing text style using shadow
    val glowingTextStyle = TextStyle(
        fontSize = 36.sp,
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        shadow = Shadow(
            color = Color(0xFF00FF00), // Light Green Glow
            offset = Offset(0f, 0f),
            blurRadius = 15f * shadowAlpha // Animate the blur radius for the glow effect
        )
    )

    // Glowing button text style with blue glow
    val glowingButtonTextStyle = TextStyle(
        fontSize = 18.sp,
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        shadow = Shadow(
            color = Color(0xFF00FFFF), // Blue Glow
            offset = Offset(0f, 0f),
            blurRadius = 10f * shadowAlpha // Glow for the button text
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .onGloballyPositioned {
                onHeightCalculated(it.size.height)
            }
    ) {
        Spacer(modifier = Modifier.height(190.dp))
        EqualizerVisualizer()
        Text(
            text = "Welcome to",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PressStart2P,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Name That Tune",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PressStart2P,
            color = Color.White,
            style = glowingTextStyle,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                soundManager.playSound()  // Play the sound on button click
                onPlayClick()  // Call the play logic
            }, modifier = Modifier
                .graphicsLayer {
                    translationX = shakeAnimation // Apply the shaking animation
                }) {
                Text("Play", fontFamily = PressStart2P, fontSize = 12.sp, style = glowingButtonTextStyle)
            }

            Button(onClick = {
                soundManager.playSound()
                onRulesClick()
            }) {
                Text("Rules", fontFamily = PressStart2P, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RulesSection(
    onSectionPositioned: (Int) -> Unit,
    scrollState: ScrollState,  // Pass the scroll state
    onScrollToTop: () -> Unit  // Trigger to scroll to top
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00FF99),  // Light Green
            Color(0xFF00C8FF)   // Light Blue
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    val gradientTextStyle = TextStyle(
        brush = gradientBrush,
        fontFamily = PressStart2P,
        fontSize = 16.sp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight + 200.dp)
            .background(Color.Red)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInRoot().y
                    onSectionPositioned(position.roundToInt())
                }
                .background(Color(0xFF005C4B))
                .padding(vertical = 40.dp, horizontal = 24.dp)
                .verticalScroll(scrollState) // Make this section scrollable
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rules",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PressStart2P,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 50.dp)
                )

                listOf(
                    "Rule 1: Listen to the song snippet",
                    "Rule 2: Choose the correct answer from the multiple choices",
                    "Rule 3: No cheating using external apps to identify the song"
                ).forEach {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontFamily = PressStart2P,
                        style = gradientTextStyle,
                        modifier = Modifier.padding(bottom = 30.dp)
                    )
                }
            }

            // Scroll back to top button
            IconButton(
                onClick = {
                    onScrollToTop()  // Trigger scroll to top in HomeScreen
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center) // Position the button at top right
                    .background(Color(0xFF00FF99), shape = CircleShape)
                    .size(70.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Back to Top",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "GitHub",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Copyright © Dennis Bandavong. All rights reserved.",
            fontSize = 10.sp,
            color = Color.White,
            fontFamily = PressStart2P
        )
    }
}

