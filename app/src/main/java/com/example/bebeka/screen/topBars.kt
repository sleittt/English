package com.example.bebeka.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bebeka.R
import com.example.bebeka.Routes
import com.example.bebeka.logik.User
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.fredokaFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBar(title:String){
    CenterAlignedTopAppBar(title = { Text(text=title,
        color = Color.White,
        fontSize = 17.sp,
        fontFamily = fredokaFonts,
        fontWeight = FontWeight.Medium) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue))

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBar(title: String, navController: NavController, routes: String){
    CenterAlignedTopAppBar(title = { Text(text=title,
        color = Color.White,
        fontSize = 17.sp,
        fontFamily = fredokaFonts,
        fontWeight = FontWeight.Medium) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
        navigationIcon = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null, modifier =  Modifier.clickable(
                    onClick = { navController.navigate(routes) }
                ),
                tint = Color(0xFFFFFFFF))
        }
    )

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBar(title: String, navController: NavController){
    CenterAlignedTopAppBar(title = { Text(text=title,
        color = Color.White,
        fontSize = 17.sp,
        fontFamily = fredokaFonts,
        fontWeight = FontWeight.Medium) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
        navigationIcon = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null, modifier =  Modifier.clickable(
                    onClick = { navController.popBackStack() }
                ),
                tint = Color(0xFFFFFFFF))
        }
    )

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topMainBar(nickname: String?, navController: NavController, currentUser: User?) {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Преобразуем ByteArray из currentUser в Bitmap
    LaunchedEffect(currentUser) {
        currentUser?.profileImage?.let { bytes ->
            imageBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
        title = {
                Column (modifier = Modifier.padding(horizontal = 24.dp)){
                    imageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    nickname?.let {
                        Text(text= stringResource(R.string.tmbn, it),
                            fontSize = 22.sp,
                            fontFamily = fredokaFonts,
                            fontWeight = FontWeight.Medium,
                            color=Color.White)
                    }
                    Text(text=stringResource(R.string.tmbb),
                        fontSize = 17.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Light,
                        color=Color(0xFFB6B6B6))
                }
        },
        modifier = Modifier.clickable(onClick = { navController.navigate(Routes.Profile.route) })
    )
}