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
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun OTPScreen(
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel? = null, flavor: String,
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
                    navController?.navigate(Routes.Phone.name)
                }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "My code is",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)

            )
            Text(
                text = "Your phone number",
                fontSize = 14.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
            val number = loginViewModel?.phoneNumber.toString()

            Text(
                text = loginViewModel?.phoneNumber.toString(),
                fontSize = 14.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
            Text(
                text = "Enter the 6 digit code sent to your phone",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)
            )
            var otpValue by remember {
                mutableStateOf("")
            }

            OtpTextField(otpText = otpValue, onOtpTextChange = { value, otpInputFilled ->
                otpValue = value
            })
            if (otpValue != "") {
                loginViewModel?.oneTime = otpValue
                loginViewModel?.checkoneTime = "checked"
            }
            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current
            TimerScreen{
                loginViewModel?.firebaseAuth(navController,context, number)
            }
            Button(
                onClick = {
                    if (otpValue.isEmpty()|| otpValue.length!=6) {
                        Toast.makeText(context, "Please enter OTP ..", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Log.e("OTP",otpValue)
                        loginViewModel?.checkSuccess(navController,flavor ,number,context)
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
fun TimerScreen(call:()->Unit) {
    var timer by remember { mutableStateOf(8) }
    var isButtonVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val text = "Resend"

    LaunchedEffect(Unit) {
            while (timer > 0) {
                delay(1000)
                timer -= 1
            }
            isButtonVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (timer > 0) {
            Text(text = "Resend OTP $timer:00 sec", fontSize = 23.sp ,color = MaterialTheme.colorScheme.primary)
        } else {
            ClickableText(
                text = AnnotatedString(text),
                style= TextStyle(fontSize = 23.sp, color = Color.Black),
                modifier = Modifier
                    .padding(16.dp)
//                    .align(Alignment.Start)
                ,
                onClick = {
                    call.invoke()
                    timer = 8
                    isButtonVisible = false
                    coroutineScope.launch(Dispatchers.Default) {
                        while (timer > 0) {
                            delay(1000)
                            timer -= 1
                        }
                        isButtonVisible = true
                    }
                })
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
