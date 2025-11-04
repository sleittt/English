package com.example.bebeka.screen.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.Routes
import com.example.bebeka.screen.topAppBar
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ListeningWord(
    val word: String,
    val transcription: String
)

val listeningWords = listOf(
    ListeningWord("cucumber", "[ kjukkmbs ]"),
    ListeningWord("apple", "[ æpl ]"),
    ListeningWord("banana", "[ bəˈnɑːnə ]"),
    ListeningWord("tomato", "[ təˈmɑːtəʊ ]"),
    ListeningWord("potato", "[ pəˈteɪtəʊ ]")
)

@Composable
fun ListeningScreen(navController: NavController) {
    var currentWord by remember { mutableStateOf(listeningWords[Random.nextInt(listeningWords.size)]) }
    var isRecording by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    val scale = remember { Animatable(1f) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            scale.animateTo(1f)
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            delay(2000L) // Эмуляция записи в течение 2 секунд
            isRecording = false
            showResult = true

            // Случайный результат для демонстрации
            isSuccess = Random.nextBoolean()
        }
    }

    Scaffold(
        topBar = {
            topAppBar(
                title = "Listening",
                navController = navController,
                routes = Routes.Main.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!showResult) {
                // Основной экран с словом
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentWord.word,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentWord.transcription,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "Please press button and say this word. Our service will check your pronunciation",
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            if (!isRecording) {
                                isRecording = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale.value),
                        enabled = !isRecording
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isRecording) "Recording..." else "Check my speech",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                // Экран результата
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your result",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = if (isSuccess) currentWord.word else "pupumber",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    if (isSuccess) {
                        Button(
                            onClick = {
                                // Переход к следующему слову
                                currentWord = listeningWords[Random.nextInt(listeningWords.size)]
                                showResult = false
                                isSuccess = false
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = "Yay! Go next",
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                showResult = false
                            },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFA500)
                            )
                        ) {
                            Text(
                                text = "Try Again",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ListeningScreenPreview() {
    ListeningScreen(rememberNavController())
}