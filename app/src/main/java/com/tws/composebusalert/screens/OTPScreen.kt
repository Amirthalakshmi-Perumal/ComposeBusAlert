package com.tws.composebusalert.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.viewmodel.DriverLoginViewModel


@Composable
fun OTPScreen(
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel? = null,
//    authorizationRepoImpl: AuthorizationRepoImpl? = null
)  {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = "My Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = {
//                    navController?.navigate(Routes.Phone.name)
                    navController?.navigate("A/OTP")
                }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
//            Spacer(modifier = Modifier.height(111.dp))
            Text(
                text = "My code is",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)

            )

//            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your phone number",
                fontSize = 14.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
//                    .padding(40.dp)
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
//            val no = "null"
//            var number by remember { mutableStateOf("") }
            val number = loginViewModel?.phoneNumber.toString()

            Text(
//                text = no.toString(),
                text = loginViewModel?.phoneNumber.toString(),
                fontSize = 14.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
//                    .padding(40.dp)
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
            Text(
                text = "Enter the 6 digit code sent to your phone",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
//                    .padding(40.dp)
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
            var otpValue by remember {
                mutableStateOf("")
            }

            OtpTextField(otpText = otpValue, onOtpTextChange = { value, otpInputFilled ->
                otpValue = value
            })
//            loginViewModel?.oneTime=otpValue
//            if (otpValue != "") {
//                authorizationRepoImpl?.oneTime = otpValue
//                authorizationRepoImpl?.checkoneTime = "checked"
//            }
            if (otpValue != "") {
                loginViewModel?.oneTime = otpValue
                loginViewModel?.checkoneTime = "checked"
            }
            Spacer(modifier = Modifier.height(16.dp))
            val text = " Resend"
            val context = LocalContext.current

            ClickableText(
                text = AnnotatedString(text),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start),
                onClick = {
//                    authorizationRepoImpl?.checkSuccess(navController,"user",context ,otpValue,"checked")
                    loginViewModel?.firebaseAuth(navController,context, number)
//                    loginViewModel?.firebaseAuth(navController, context, number)
                })

            Button(
                onClick = {
//                    if (otpValue.isEmpty() || otpValue.length!=5) {
                    if (otpValue.isEmpty()|| otpValue.length!=6) {
                        Toast.makeText(context, "Please enter OTP ..", Toast.LENGTH_SHORT)
                            .show()
                    } else {
//                        authorizationRepoImpl?.checkoneTime = "checked"
                        Log.e("OTP",otpValue)
//                        authorizationRepoImpl?.checkSuccess(navController,"user",context ,otpValue,"checked")
//                        loginViewModel?.checkSuccess(navController,"driver",context,otpValue,"checked" )
                        loginViewModel?.checkSuccess(navController,"driver" ,number,context)
//                        navController?.navigate(Routes.DriverSelectRouteScreen.name)

                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RectangleShape)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),

                ) {
                Text(
                    "CONTINUE ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black
                )
            }
        }
    }
}



@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    BasicTextField(modifier = modifier
        .fillMaxWidth()
        .padding(15.dp),
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) {
                onOtpTextChange.invoke(it, it.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index, text = otpText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        })
}


@Composable
private fun CharView(
    index: Int, text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(40.dp)
            .height(45.dp)
            .border(
                3.dp, when {
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.primary
                }, RoundedCornerShape(8.dp)
            )
            .padding(5.dp),
        text = char,
        style = MaterialTheme.typography.titleLarge,
        color = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primary
        },
        textAlign = TextAlign.Center
    )
}
