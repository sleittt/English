package com.example.bebeka.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bebeka.Routes
import com.example.bebeka.ui.theme.disabledBox
import com.example.bebeka.ui.theme.enabledBox
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts

@Composable
fun onBoarding(modifier: Modifier = Modifier, navController: NavController, image: Int, header:String, text:String,buttonText:String, route: String, progress:@Composable () -> Unit ) {

    Column(modifier= modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(120.dp))
            Image(ImageBitmap.imageResource(image), contentDescription = null, modifier = Modifier.size(300.dp))
        Spacer(Modifier.height(60.dp))
        progress()
        Spacer(Modifier.height(30.dp))
        Text(
            text = header,
            fontSize = 22.sp,
            fontFamily = fredokaFonts,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            fontFamily = fredokaFonts,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { navController.navigate(route) },
            modifier = Modifier.height(56.dp).fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = enabledButton)
        ) { Text(text=buttonText,
            fontSize = 20.sp,
            fontFamily = fredokaFonts,
            fontWeight = FontWeight.Medium) }
        TextButton(onClick = { navController.navigate(Routes.langSelect.route) }) {
            Text(
                text="Skip onboarding",
                fontSize = 15.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Light
            )
        }
    }
}
@Composable
fun imageOnBoard1(){
    Row{
        progressBarOn()
        progressBarOff()
        progressBarOff()
    }
}
@Composable
fun imageOnBoard2(){
    Row(){
        progressBarOff()
        progressBarOn()
        progressBarOff()
    }
}
@Composable
fun imageOnBoard3(){
    Row(){
        progressBarOff()
        progressBarOff()
        progressBarOn()
    }
}

@Composable
fun progressBarOn(){
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                color = enabledBox
            )
    )
    Spacer(Modifier.width(6.dp))
}
@Composable
fun progressBarOff(){
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                color = disabledBox
            )
    )
    Spacer(Modifier.width(6.dp))

}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    val navController = rememberNavController()
//    ProfikTheme {
//        onBoarding(Modifier.fillMaxSize(), { imageOnBoard1(navController = navController) })
//    }
//}
@Preview
@Composable
fun prew(){
    imageOnBoard1()
}