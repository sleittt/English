package com.example.profik.screen.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.BackNavigation
import com.example.bebeka.R
import com.example.bebeka.Routes
import com.example.bebeka.firebase.FirestoreService
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.PointsCalculator
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.UserRepository
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.launch

data class Animal(
    val imageRes: Int,
    val correctAnswer: String
)

val animals = listOf(
    Animal(R.drawable.bird, "bird"),
    Animal(R.drawable.cat, "cat"),
    Animal(R.drawable.rabbit, "rabbit"),
    Animal(R.drawable.racoon, "racoon"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun animalScreen(navController: NavController, firestoreService: FirestoreService) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Инициализация репозитория
    val db = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { UserRepository(db.userDao(), firestoreService) }

    // Получение ID текущего пользователя
    val currentUserId = remember { SessionManager.getCurrentUserId(context) }

    var currentAnimal by remember { mutableStateOf(animals.random()) }
    var message by remember { mutableStateOf("") }
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
    BackNavigation(navController, Routes.Main.route)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Guess the animal",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
                navigationIcon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null, modifier = Modifier.clickable(
                            onClick = { navController.navigate(Routes.Main.route) }
                        ),
                        tint = Color(0xFFFFFFFF))
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                bitmap = ImageBitmap.imageResource(currentAnimal.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(328.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Text(text="Write who is on image",
                fontSize = 15.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
            TextField(value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val isCorrect = message.equals(currentAnimal.correctAnswer, ignoreCase = true)

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

                    if (isCorrect) {
                        navController.navigate(Routes.AnimalGuess.createRoute("correct"))
                    } else {
                        navController.navigate(Routes.AnimalGuess.createRoute(currentAnimal.correctAnswer))
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                Text(text = stringResource(R.string.check),
                    fontSize = 15.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}

//будет передаваться картинка, в зависимости от правильности ответа?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun animalGuess(navController: NavController, answer: String) {
    BackNavigation(navController, Routes.Main.route)
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text= stringResource(R.string.animal),
                color = Color.White,
                fontSize = 22.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = if (answer=="correct") Color(0xFF5BA890) else Color(0xFFD6185D)),
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            if (answer == "correct") {
                Text(text="\uD83C\uDF89",
                    fontSize = 160.sp,)
                Spacer(Modifier.height(30.dp))
                Text(text=stringResource(R.string.animalright),
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(30.dp))
            } else {
                Text(text="\uD83D\uDE3F",
                    fontSize = 160.sp,)
                Spacer(Modifier.height(30.dp))
                Text(text=stringResource(R.string.animalfalse, answer),
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(30.dp))
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = enabledButton)
                    ) {
                        Text(text= stringResource(R.string.again),
                            fontSize = 20.sp,
                            fontFamily = fredokaFonts,
                            fontWeight = FontWeight.Medium)
                    }
                Spacer(Modifier.height(10.dp))
            }
            Button(
                onClick = {
                    navController.navigate(Routes.Animal.route)
                },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                Text(text= stringResource(R.string.next),
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview
@Composable
fun preview() {
    animalGuess(rememberNavController(), "correct" )
}