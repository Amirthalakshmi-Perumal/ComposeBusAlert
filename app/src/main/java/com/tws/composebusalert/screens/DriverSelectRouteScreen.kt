package com.tws.composebusalert.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.MainActivity
import com.tws.composebusalert.data.UserStore
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelectRouteScreen(navController: NavController? = null, loginViewModel: DriverLoginViewModel? = null,) {
    val sharedPrefFile = "kotlinsharedpreference"
    val context= LocalContext.current
//        val context= MainActivity().applicationContext
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile,
        Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
    editor.putInt("id_key",22)
    editor.putString("name_key","name")
    editor.apply()
    Log.e("TAGG","name+id.toString()")
    Column {
        TopAppBar(
            title = {
                Text(
                    text = "TITLE",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally)
                        .padding(10.dp)
                )
            },
            modifier = Modifier.height(50.dp),
            colors = TopAppBarDefaults.smallTopAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.onSecondary,
            )
        )
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        SearchView(textState,navController,loginViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(state: MutableState<TextFieldValue>,navController: NavController? = null,loginViewModel: DriverLoginViewModel? = null) {
    val context = LocalContext.current
    val store = UserStore(context)
    val tokenText = store.getAccessToken.collectAsState(initial = "")
    val tokenValue = remember {
        mutableStateOf(TextFieldValue())
    }


    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 18.dp, 16.dp, 18.dp)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(12.dp) // Set the border shape to RoundedCornerShape
            ),
//            .border(1.dp, Color.Black),
//            .clip(CircleShape),
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            store.saveToken(tokenValue.value.text)
                        }
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    },
                    ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(2.dp)
                            .size(25.dp)
                    )
                }
            }
        },
        singleLine = true,
//        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(
//            textColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            containerColor = Color.White,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )

    val number = 18
    val messages: List<String> = listOf("HAI", "Hello", "AAA")
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(-1) }
    LazyColumn(state = listState) {
        items(items = messages) { message ->
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RectangleShape // Set the border shape to RoundedCornerShape
                    )
                    .padding(16.dp)
                    .selectable(
                        selected = true,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                store.saveToken(tokenValue.value.text)
                            }
//                            loginViewModel?.getDriverDetailsVM()
                            navController?.navigate(Routes.DriverDashboard.name)
                        },
                    ),
            )
        }
    }


}



/*

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    SearchView(textState)
}
*/
/*
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black) // Add border to the TextField
            .padding(4.dp), // Add padding to the TextField
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        trailingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
    )*/
