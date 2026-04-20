package com.example.bebeka.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bebeka.Routes
import com.example.bebeka.data.Prefs
import com.example.bebeka.data.updateLocale
import com.example.bebeka.ui.theme.enabledBox
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import com.example.bebeka.utils.DebugLogger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectScreen(
    navController: NavController,
    firstChange: Boolean = true
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(-1) }

    val languages = listOf(
        "English" to "en",
        "Russian" to "ru"
    )

    LaunchedEffect(Unit) {
        val prefs = Prefs(context)
        val savedLanguage = prefs.language
        DebugLogger.d("LanguageSelect", "Loaded saved language: $savedLanguage")
        val index = languages.indexOfFirst { it.second == savedLanguage }
        if (index != -1) {
            selected = index
            DebugLogger.d("LanguageSelect", "Restored selected index: $index")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topAppBar("Language select")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(7.dp))
            Text(
                text = "What is your Mother language?",
                fontSize = 22.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(7.dp))

            selected = SelectableButtons(
                languages = languages.map { it.first }.toTypedArray(),
                currentSelected = selected
            )

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 30.dp)
            ) {
                Button(
                    onClick = {
                        if (selected > -1) {
                            val prefs = Prefs(context)
                            val languageCode = languages[selected].second
                            DebugLogger.d("LanguageSelect", "User selected language: ${languages[selected].first} ($languageCode)")

                            prefs.language = languageCode
                            updateLocale(context, languageCode)

                            if (firstChange) {
                                DebugLogger.d("LanguageSelect", "First language selection -> navigate to Signup1")
                                navController.navigate(Routes.Signup1.route)
                            } else {
                                DebugLogger.d("LanguageSelect", "Language changed later -> navigate to Main")
                                navController.navigate(Routes.Main.route)
                            }
                        } else {
                            DebugLogger.d("LanguageSelect", "No language selected, button disabled")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = enabledButton
                    ),
                    enabled = selected > -1
                ) {
                    Text(
                        text = "Choose",
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableButtons(
    languages: Array<String>,
    currentSelected: Int
): Int {
    var selectedIndex by remember { mutableStateOf(currentSelected) }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        languages.forEachIndexed { i, language ->
            val isSelected = i == selectedIndex
            Button(
                onClick = {
                    selectedIndex = i
                    DebugLogger.d("LanguageSelect", "Button clicked: $language, index=$i")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isSelected) Color(0xFFFFF6EB) else enabledBox
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = language,
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
    return selectedIndex
}