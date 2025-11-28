package com.example.bebeka.screen.games

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer.createOnDeviceSpeechRecognizer
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.Routes

import com.example.bebeka.screen.topAppBar
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.delay
import java.io.File
import kotlin.random.Random
import java.util.concurrent.Executors
import android.speech.RecognitionListener.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.bebeka.logik.SpeechRecognitionListener

data class ListeningWord(
    val word: String,
    val transcription: String
)

val listeningWords = listOf(
    ListeningWord("cucumber", "[ 'kju:kʌmbə ]"),
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
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    val context = LocalContext.current
    val scale = remember { Animatable(1f) }
    var recognizedText by remember { mutableStateOf("") }


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
                .padding(horizontal = 20.dp,vertical= 30.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (!showResult) {
                // Основной экран с словом
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentWord.word,
                        fontSize = 22.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentWord.transcription,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "Please press button and say this word. Our service will check your pronunciation",
                        fontSize = 22.sp,
                        fontFamily = fredokaFonts,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            if (isRecording) {
                                val speechRecognizer = createOnDeviceSpeechRecognizer(context)
                                val speechListener = SpeechRecognitionListener(
                                    onResultCallback = { result ->
                                        recognizedText = result // Сохраняем результат
                                        isSuccess = recognizedText.equals(currentWord.word, ignoreCase = true) // Проверяем совпадение
                                        showResult = true // Показываем результат
                                        isRecording = false
                                    }
                                )
                                speechRecognizer.setRecognitionListener(speechListener)
                                val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
                                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                                    }
                                }
                                speechRecognizer.startListening(recognizerIntent)
                            }else {
                                // Останавливаем запись
                                mediaRecorder?.apply {
                                    stop()
                                    release()
                                }
                                mediaRecorder = null
                                isRecording = false
                            }
                        },
                        modifier = Modifier.height(56.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = enabledButton)
                    ) { Text(text="Check my speech",
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium) }
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
                        text = recognizedText,
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