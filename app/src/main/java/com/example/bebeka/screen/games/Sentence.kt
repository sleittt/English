package com.example.bebeka.screen.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import com.example.bebeka.BackNavigation
import com.example.bebeka.R
import com.example.bebeka.Routes
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.PointsCalculator
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.UserRepository
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SentenceData(
    val id: Int,
    val english: String,
    val russian: String
)

val sentences = arrayOf(
    SentenceData(1, "I ___ a student", "Я студент"),
    SentenceData(2, "She ___ to school every day", "Она ходит в школу каждый день"),
    SentenceData(3, "We ___ watching TV", "Мы смотрим телевизор"),
    SentenceData(4, "He ___ a new car", "У него новая машина"),
    SentenceData(5, "They ___ playing football", "Они играют в футбол"),
    SentenceData(6, "The cat ___ on the mat", "Кот сидит на коврике"),
    SentenceData(7, "My brother ___ basketball", "Мой брат играет в баскетбол"),
    SentenceData(8, "We ___ English at school", "Мы учим английский в школе")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sentence(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    BackNavigation(navController, Routes.Main.route)

    // Инициализация репозитория
    val db = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { UserRepository(db.userDao()) }

    // Получение ID текущего пользователя
    val currentUserId = remember { SessionManager.getCurrentUserId(context) }

    // Данные для предложений


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.game),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
                navigationIcon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            onClick = { navController.navigate(Routes.Main.route) }
                        ),
                        tint = Color.White
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var userAnswer by remember { mutableStateOf("") }
            var isAnswered by remember { mutableStateOf(false) }
            var isCorrect by remember { mutableStateOf(false) }
            var currentSentenceIndex by remember { mutableStateOf(Random.nextInt(sentences.size)) }
            var userPoints by remember { mutableStateOf(0) }

            // Загрузка текущих очков пользователя при старте
            LaunchedEffect(currentUserId) {
                if (currentUserId != -1L) {
                    val user = userRepository.getUserById(currentUserId)
                    user?.let {
                        userPoints = it.points
                    }
                }
            }

            val currentSentence = sentences[currentSentenceIndex]
            val missingWord = "___"
            val fullEnglishSentence = currentSentence.english
            val englishParts = fullEnglishSentence.split(missingWord)
            val correctAnswer = when {
                fullEnglishSentence.contains("___ a student") -> "am"
                fullEnglishSentence.contains("___ to school") -> "goes"
                fullEnglishSentence.contains("___ watching") -> "are"
                fullEnglishSentence.contains("___ a new car") -> "has"
                fullEnglishSentence.contains("___ playing") -> "are"
                fullEnglishSentence.contains("___ on the") -> "is"
                fullEnglishSentence.contains("___ basketball") -> "plays"
                fullEnglishSentence.contains("___ English") -> "learn"
                else -> ""
            }

            // Отображение предложения с пропуском
            Text(
                text = fullEnglishSentence,
                fontSize = 24.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Текстовое поле для ввода ответа
            TextField(
                value = userAnswer,
                onValueChange = { if (!isAnswered) userAnswer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isAnswered,
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.missing),
                        fontFamily = fredokaFonts,
                        fontSize = 16.sp
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = if (isCorrect) Color.Green
                    else Color.Red
                ),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.weight(1F))

            // Кнопка проверки/следующий
            Button(
                onClick = {
                    if (userAnswer.isNotBlank()) {
                        if (!isAnswered) {
                            isAnswered = true
                            isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)

                            // Расчет и сохранение баллов
                            val pointsEarned = PointsCalculator.calculatePoints(isCorrect)

                            if (currentUserId != -1L && pointsEarned > 0) {
                                scope.launch {
                                    userRepository.updateUserPoints(currentUserId, pointsEarned)
                                    // Обновляем локальное состояние
                                    val updatedUser = userRepository.getUserById(currentUserId)
                                    updatedUser?.let {
                                        userPoints = it.points
                                    }
                                }
                            }
                        } else {
                            // Сбрасываем состояние для следующего предложения
                            userAnswer = ""
                            isAnswered = false
                            isCorrect = false
                            currentSentenceIndex = Random.nextInt(sentences.size)
                        }
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton
                ),
                enabled = userAnswer.isNotBlank()
            ) {
                Text(
                    text = if (isAnswered) stringResource(R.string.next) else stringResource(R.string.check),
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun SentencePreview() {
    Sentence(rememberNavController())
}