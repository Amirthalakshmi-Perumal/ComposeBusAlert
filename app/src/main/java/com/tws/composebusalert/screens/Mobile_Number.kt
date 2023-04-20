package com.tws.composebusalert.screens

import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ScaffoldState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mobile_Number(
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel? = null,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreData(context)
    val savedNo = dataStore.getNo.collectAsState(initial = "")


    val view = LocalView.current
    loginViewModel?.validationError?.observeAsState {
        showErrorMessage("Failed Registry", view)
    }


    val msg = loginViewModel?.onSuccess?.collectAsState()
    var bar = true
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
                    navController?.navigate(Routes.Dashboard.name)
                }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Enter registered mobile number",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(38.dp)
                    .align(CenterHorizontally)
            )
            var number by remember { mutableStateOf("") }

            OutlinedTextField(
                value = number,
                onValueChange = { newValue ->
                    if (newValue.length <= 10) {
                        number = newValue
                    }
                },
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Text(text = "+91")
                },
                keyboardActions = KeyboardActions(onNext = { }, onDone = { }),
                maxLines = 1,
                modifier = Modifier.align(CenterHorizontally)
            )
            loginViewModel?.phoneNumber = number
            Text(
                text = "The verified phone number can be used to login. When you tap submit,will receive a text with verification code for login.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(40.dp)
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current
            Button(
                onClick = {
                    if (TextUtils.isEmpty(number)) {
                        Toast.makeText(context, "Please enter phone number..", Toast.LENGTH_SHORT)
                            .show()

                    } else if (number.length == 10) {
                        Toast.makeText(context, "Verifying..", Toast.LENGTH_SHORT).show()
//                        loginViewModel?.signIn(
//                            "+91", loginViewModel.phoneNumber, "driver", navController, context
//                        )

                        loginViewModel?.signIn(
                            "+91", number, "driver", navController, context
                        )
                        navController?.navigate(Routes.OTP.name)

                        scope.launch {
                            dataStore.saveNo(number)
                        }
                        Log.e("PHONE NO", savedNo.value!!)
                    } else {
                        showErrorMessage("Snack Bar", view)
                        Toast.makeText(context, "Invalid Phone Number..", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier
                    .align(CenterHorizontally)
                    .clip(RectangleShape),
                shape = RoundedCornerShape(12.dp),

                ) {
                Text(
                    " Submit", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black
                )
            }
            if (!msg?.value.isNullOrEmpty()) {
                msg?.value?.let {
                    ShowToast(content = it, bar)
                }
            }
            if (bar) {
                ShowToast(content = "Snack Bar", bar)
                bar = false
            }

        }

    }


}

fun showErrorMessage(message: String, view: View) {
    val snack = com.google.android.material.snackbar.Snackbar.make(
        view, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
    )
    snack.setAction("OK") {
        snack.dismiss()
    }
    snack.show()
}

@Composable
fun ShowToast(
    content: String, onClick: Boolean
) {
//    val context = LocalContext.current
//    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

    /* Snackbar(action = {
         // Optional action button
         Text(text = "Dismiss")
     }, content = {
         Text(text = content)
     })*/
    var bar = onClick
//    if(bar){
//        Snackbar(
//            modifier = Modifier.padding(4.dp),
//            action = {
//                TextButton(onClick = {
//                    bar=false
//
//                }) {
//                    Text(text = "Remove")
//                }
//            }

//        ) {
//            Text(text = "This is a basic Snackbar with action item")
//        }
//    }

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    /*Scaffold(scaffoldState = scaffoldState) {
        Button(onClick = {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "This is your message",
                    actionLabel = "Do something"
                )
            }
        }) {
            Text(text = "Click me!")
        }
    }*/
}
