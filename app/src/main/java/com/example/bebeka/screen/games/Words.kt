package com.example.bebeka.screen.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.Routes
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.PointsCalculator
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.UserRepository
import com.example.bebeka.screen.topAppBar
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Words(
        val id:Int,
        val word:String,
        val onRus:String
        )
val words=arrayOf(
    Words(1,"cat","кот"),
    Words(2,"dog","собака"),
    Words(3,"fly","муха"),
    Words(4,"fish","рыба"),
    Words(5,"rabbit","кролик"),
    Words(6,"water","вода"),
    Words(7,"bottle","бутылка"),
    Words(8,"plant","растение"),
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Инициализация репозитория
    val db = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { UserRepository(db.userDao()) }

    // Получение ID текущего пользователя
    val currentUserId = remember { SessionManager.getCurrentUserId(context) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text="Word practice",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
                navigationIcon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null, modifier =  Modifier.clickable(
                            onClick = { navController.navigate(Routes.Main.route) }
                        ),
                        tint = Color(0xFFFFFFFF))
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(24.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {

            var selectedIndex by remember { mutableStateOf(-1) }
            var isAnswered by remember { mutableStateOf(false) }
            var currentWordId by remember { mutableStateOf(Random.nextInt(words.size)) }
            var currentEnglish by remember { mutableStateOf(Random.nextBoolean()) }
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

            val word = if (currentEnglish) words[currentWordId].word else words[currentWordId].onRus
            val correctAnswer = if (currentEnglish) words[currentWordId].onRus else words[currentWordId].word

            val answers = remember(currentWordId, currentEnglish) {
                val otherAnswers = mutableListOf<String>()
                while (otherAnswers.size < 3) {
                    val randomIndex = Random.nextInt(words.size)
                    val randomWord = words[randomIndex]
                    val randomAnswer = if (currentEnglish) randomWord.onRus else randomWord.word
                    if (randomAnswer != correctAnswer && !otherAnswers.contains(randomAnswer)) {
                        otherAnswers.add(randomAnswer)
                    }
                }
                otherAnswers.apply { add(correctAnswer) }.shuffled().toTypedArray()
            }

            val correctAnswerIndex = answers.indexOf(correctAnswer)

            Text(text=word,
                fontSize = 28.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
            if (currentEnglish){
                Text(text="Transcription",
                    fontSize = 17.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(40.dp))
            Column {
                answers.forEachIndexed { index, answerText ->
                    val isSelected = index == selectedIndex
                    val isCorrect = index == correctAnswerIndex

                    // Определяем цвет кнопки в зависимости от состояния
                    val buttonColor = when {
                        isAnswered && isCorrect -> Color(0xFF5BA890) // Правильный ответ всегда зеленый
                        isAnswered && isSelected && !isCorrect -> Color(0xFFF76400) // Выбранный неправильный ответ - оранжевый
                        isSelected -> enabledButton // Выбранный ответ до проверки
                        else -> Color(0xFFE5E5E5) // Невыбранные ответы
                    }

                    Button(
                        onClick = {
                            if (!isAnswered) {
                                selectedIndex = index
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        )
                    ) {
                        Text(text=answerText,
                            fontSize = 20.sp,
                            fontFamily = fredokaFonts,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.weight(1F))
            Button(onClick = {
                if(selectedIndex != -1) {
                    if (!isAnswered) {
                        isAnswered = true

                        // Расчет и сохранение баллов
                        val isCorrect = (selectedIndex == correctAnswerIndex)
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
                        // Сбрасываем состояние для следующего слова
                        selectedIndex = -1
                        isAnswered = false
                        currentWordId = Random.nextInt(words.size)
                        currentEnglish = Random.nextBoolean()
                    }
                }
            },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                Text(text=if (isAnswered && selectedIndex != -1) "Next" else "Check",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
            }
        }
    }
}


@Preview
@Composable
fun prew(){
    WordsScreen(rememberNavController())
}