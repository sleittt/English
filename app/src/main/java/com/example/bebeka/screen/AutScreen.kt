package com.example.bebeka.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bebeka.R
import com.example.bebeka.Routes
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.User
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage1Screen(
    navController: NavController,
    context: Context,
    onContinue: (String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topAppBar(title = "Signup", navController = navController, routes = Routes.Login.route)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Create an account",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            // First Name field
            Text(text="First name",
                fontSize = 15.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Light
            )
            Spacer(Modifier.height(5.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = false
                },
                label = { Text(text="Your First Name",
                    color=Color(0x80656872),
                    fontSize = 15.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Light) },
                modifier = Modifier.fillMaxWidth(),
                isError = firstNameError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp)
            )
            if (firstNameError) {
                Text(
                    text = "First name is required",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Last Name field
            Text(text="Last name",
                fontSize = 15.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Light)
            Spacer(Modifier.height(5.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    lastNameError = false
                },
                label = { Text(text="Your Last Name",
                    color=Color(0x80656872),
                    fontSize = 15.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Light) },
                modifier = Modifier.fillMaxWidth(),
                isError = lastNameError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp)
            )
            if (lastNameError) {
                Text(
                    text = "Last name is required",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            Text(text="Email address",
                fontSize = 15.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Light)
            Spacer(Modifier.height(5.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { Text(text="Email",
                    color=Color(0x80656872),
                    fontSize = 15.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Light) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp)
            )
            if (emailError) {
                Text(
                    text = "Invalid email format",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Button(
                onClick = {
                    var hasError = false
                    if (firstName.isBlank()) {
                        firstNameError = true
                        hasError = true
                    }
                    if (lastName.isBlank()) {
                        lastNameError = true
                        hasError = true
                    }
                    if (!isValidEmail(email)) {
                        emailError = true
                        hasError = true
                    }

                    if (!hasError) {
                        onContinue(firstName, lastName, email)
                    }
                },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                Text(text="Continue",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ")
                Text(
                    text = "Login",
                    color = enabledButton,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.Login.route)
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage2Screen(
    navController: NavController,
    context: Context,
    firstName: String,
    lastName: String,
    email: String
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var termsError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topAppBar(title = "Signup", navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "Choose a Password",
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))

            // Password field
            PasswordFieldWithValidation(
                title = "Password",
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    errorMessage = null
                },
                isError = passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password field
            PasswordField(
                title = "Confirm Password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                    errorMessage = null
                },
                isError = confirmPasswordError
            )
            if (confirmPasswordError) {
                Text(
                    text = "Passwords don't match",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms agreement
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it }
                )
                Text(text="I have made myself acquainted with the Rules and accept all its provisions,",
                    fontSize = 17.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Light)
            }
            if (termsError) {
                Text(
                    text = "You must accept terms and conditions",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up button
            Button(
                onClick = {
                    val isPasswordValid = isPasswordStrong(password)
                    val doPasswordsMatch = password == confirmPassword

                    passwordError = !isPasswordValid
                    confirmPasswordError = !doPasswordsMatch
                    termsError = !acceptedTerms

                    if (isPasswordValid && doPasswordsMatch && acceptedTerms) {
                        isLoading = true
                        errorMessage = null

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val existingUser = db.userDao().getUserByEmail(email)
                                if (existingUser != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading = false
                                        errorMessage = "User with this email already exists"
                                    }
                                } else {
                                    val newUser = User(
                                        email = email,
                                        password = password,
                                        username = "$firstName $lastName"
                                    )
                                    val userId = db.userDao().insertUser(newUser)

                                    // Сохраняем ID пользователя в сессии
                                    SessionManager.saveCurrentUserId(context, userId)

                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading = false
                                        navController.navigate(Routes.Main.route) {
                                            popUpTo(Routes.Signup1.route) { inclusive = true }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    isLoading = false
                                    errorMessage = "Registration failed: ${e.message}"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Sign Up")
                }
            }
        }
    }
}

@Composable
fun LogInScreen(navController: NavController, context: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }

    Scaffold(topBar = {
        topAppBar(title="Login",
        navController = navController,
        routes = Routes.langSelect.route)
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                ImageBitmap.imageResource(R.drawable.login),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Text(text = "For free, join now and start learning",
                fontSize = 22.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                    errorMessage = null
                },
                label = { Text(text="Email",
                    color=Color(0x80656872),
                    fontSize = 15.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Light) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF080E1E0D),
                    unfocusedContainerColor = Color(0xFF080E1E0D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp)
            )
            if (emailError) {
                Text(
                    text = "Invalid email format",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                title = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                isError = false
            )

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val isEmailValid = isValidEmail(email)
                    emailError = !isEmailValid

                    if (isEmailValid && password.isNotEmpty()) {
                        isLoading = true
                        errorMessage = null

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val user = db.userDao().getUser(email, password)
                                if (user != null) {
                                    // Сохраняем ID пользователя в сессии
                                    SessionManager.saveCurrentUserId(context, user.id)

                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading = false
                                        navController.navigate(Routes.Main.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isLoading = false
                                        errorMessage = "Invalid email or password"
                                    }
                                }
                            } catch (e: Exception) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    isLoading = false
                                    errorMessage = "Login failed: ${e.message}"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Log In")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Not you member? ")
                Text(
                    text = "Signup",
                    color = enabledButton,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.Signup1.route)
                    }
                )
            }
        }
    }
}


@Composable
fun PasswordFieldWithValidation(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false
) {
    val checked = remember { mutableStateOf(false) }

    Column {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 15.sp,
            fontFamily = fredokaFonts,
            fontWeight = FontWeight.Light
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (checked.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconToggleButton(
                    checked = checked.value,
                    onCheckedChange = { checked.value = it }
                ) {
                    Icon(
                        imageVector = if (checked.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (checked.value) "Hide password" else "Show password"
                    )
                }
            },
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF080E1E0D),
                unfocusedContainerColor = Color(0xFF080E1E0D),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp)
        )

        // Password requirements indicator
        //PasswordRequirementsIndicator(password = value)

        if (isError) {
            Text(
                text = "Password doesn't meet requirements",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PasswordRequirementsIndicator(password: String) {
    val requirements = listOf(
        Requirement("8+ characters", password.length >= 8),
        Requirement("Uppercase letter", password.any { it.isUpperCase() }),
        Requirement("Lowercase letter", password.any { it.isLowerCase() }),
        Requirement("Digit", password.any { it.isDigit() }),
        Requirement("Space", password.any { it.isWhitespace() }),
        Requirement("Special character", password.any {
            !it.isLetterOrDigit() && !it.isWhitespace()
        })
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Password must contain:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        var error=false
        requirements.forEach { requirement ->
            if (!requirement.met&&!error) {
                RequirementItem(requirement)
                error=true
            }
        }
    }
}

@Composable
fun RequirementItem(requirement: Requirement) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (requirement.met) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (requirement.met) Color.Green else Color.Red,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = requirement.text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (requirement.met) Color.Green else Color.Red
        )
    }
}

data class Requirement(val text: String, val met: Boolean)

// Validation functions
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]+\$".toRegex()
    return emailRegex.matches(email)
}

fun isPasswordStrong(password: String): Boolean {
    if (password.length < 8) return false

    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpace = password.any { it.isWhitespace() }
    val hasSpecialChar = password.any {
        !it.isLetterOrDigit() && !it.isWhitespace()
    }

    return hasUpperCase && hasLowerCase && hasDigit && hasSpace && hasSpecialChar
}

// Basic PasswordField component
@Composable
fun PasswordField(
    title: String,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false
) {
    val checked = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title,
            fontSize = 15.sp,
            fontFamily = fredokaFonts,
            fontWeight = FontWeight.Light)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (checked.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconToggleButton(
                    checked = checked.value,
                    onCheckedChange = { checked.value = it }
                ) {
                    Icon(
                        imageVector = if (checked.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (checked.value) "Hide password" else "Show password"
                    )
                }
            },
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF080E1E0D),
                unfocusedContainerColor = Color(0xFF080E1E0D),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

