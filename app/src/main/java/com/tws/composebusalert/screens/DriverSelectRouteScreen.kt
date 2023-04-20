package com.tws.composebusalert.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DriverSelectRouteScreen(
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel,
    lifecycleOwner: LifecycleOwner
) {

    Log.e("DriverSelectRouteScreen", loginViewModel.listResponse.toString())

    Column {

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


        val textState = remember { mutableStateOf(TextFieldValue("")) }
        SearchView(textState, loginViewModel, lifecycleOwner = lifecycleOwner)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    state: MutableState<TextFieldValue>,
    loginViewModel: DriverLoginViewModel? = null,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val dataStore = StoreData(context)
    val storedRouteId = dataStore.getrouteId.collectAsState(initial = "")
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
                width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(12.dp)
            ),

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
                        /* scope.launch {
                             dataStore.screen("DashBoard Screen")
                         }*/
                        CoroutineScope(Dispatchers.IO).launch {
                            loginViewModel?.getRouteList("")
                            dataStore.screen("DashBoard Screen")
                        }
                        state.value = TextFieldValue("")
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
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )

    val listState = rememberLazyListState()
    var messages: List<RouteListResponse>? = null


    LaunchedEffect(lifecycleOwner) {
        loginViewModel?.getRouteList("")
        Log.e("DriverSelectRouteScreen", loginViewModel?.listResponse.toString())

        dataStore.screen("DashBoard Screen")

    }
    Log.e("RouteID", storedRouteId.value)
    if (loginViewModel?.getRouteList("") != null) {
        messages = loginViewModel.getRouteList("")
    }

    Log.e("DDDDD", loginViewModel?.listResponse.toString())
    Log.e("DDDDDFFFF message", messages.toString())

    if (messages == null) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(104.dp)
                    .padding(20.dp),
                color = colorResource(id = R.color.purple_200),
                strokeWidth = Dp(value = 6F)
            )
        }

    }

    if (messages != null) {
        SimpleRadioButtonComponent(messages)
        /*  LazyColumn(state = listState) {
              items<RouteListResponse>(items = messages ?: emptyList()) { message ->
                  Log.e("DriverselectRoute", messages.size.toString())
                  Text(
                      text = message.name,
                      modifier = Modifier
                          .fillMaxWidth()
  //                    .clip(RoundedCornerShape(18.dp))
                          .background(Color.White)
                          .padding(16.dp, 0.dp, 16.dp, 0.dp)
                          .border(
                              width = 1.dp,
                              color = Color.LightGray,
                              shape = RectangleShape
                          )
                          .padding(16.dp)
                          .selectable(
                              selected = true,
                              onClick = {
                                  scope.launch {
                                      dataStore.saverouteId(message.id)
                                      dataStore.screen("DashBoard Screen")
                                  }
  //                            Log.e("RouteID", storedRouteId.value)

  //                            loginViewModel?.updateRouteSelection(selectedIndex, "isChecked")
  //                            loginViewModel?.getDriverDetailsVM()

                              },
                          ),
                  )

              }
          }*/
    }


}

@Composable
fun SimpleRadioButtonComponent(radio: List<RouteListResponse>) {
    val radioOptions = radio
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[2]) }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var messages: List<RouteListResponse>? = null
    LazyColumn(state = listState) {
        items<RouteListResponse>(items = messages ?: emptyList()) { message ->
            if (messages != null) {
                Log.e("DriverselectRoute", messages.size.toString())
                Column {
                    radioOptions.forEach { text ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) })
                                .padding(horizontal = 16.dp)

                        ) {
                            Text(
                                text = message.name, modifier = Modifier.weight(1f)
                            )
                            RadioButton(selected = (text == selectedOption), onClick = {
                                onOptionSelected(text)
                                Toast.makeText(context, message.name, Toast.LENGTH_LONG).show()
                            })
                        }
                    }
                }
            }
        }
        /*   Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column {
            radioOptions.forEach { text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) }
                        )
                        .padding(horizontal = 16.dp)

                ) {
                    Text(
                        text = text,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }*/
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelectRouteScreen(navController: NavController? = null, loginViewModel: DriverLoginViewModel,) {
    val sharedPrefFile = "kotlinsharedpreference"
    val context= LocalContext.current
//        val context= MainActivity().applicationContext
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile,
        Context.MODE_PRIVATE)
//    loginViewModel?.getRouteList("")
//    loginViewModel?.listResponse
*/
/*    Handler().postDelayed({
        Log.e("DriverselectRoute", loginViewModel?.listResponse.toString())
    }, 40000)*//*

    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//    editor.putInt("id_key",22)
//    editor.putString("name_key","name")
//    editor.apply()
//    Log.e("TAGG","name+id.toString()")
    val routeListState = remember { mutableStateOf<List<RouteListResponse>>(emptyList()) }

    */
/*LaunchedEffect(Unit) {
        loginViewModel?.getRouteList("")
        Log.e("DriverselectRouteeeee", loginViewModel?.listResponse.toString())
    }*//*

//    loginViewModel?.routeList?.observeAsState()?.value?.let { routeList ->
//        routeListState.value = routeList
//    }
    var listResponse11: List<RouteListResponse>? = null
    LaunchedEffect(Unit) {

        editor.putInt("id_key",22)
        editor.putString("name_key","name")
        editor.apply()
        Log.e("TAGG","name+id.toString()")



        loginViewModel.getRouteList()
        listResponse11= loginViewModel.listResponse

        Log.e("BBBBBB", listResponse11.toString())
//        Log.e("TOKEN", savedEmail.toString())
//        Log.e("TOKENAAA", savedEmail.value.toString())
//        Log.e("TOKENAAADDD", savedEmail.value!!)

    }
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
     */
/*   if (listResponse11==null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(104.dp)
                        .padding(20.dp),
                    color = colorResource(id = R.color.purple_200),
                    strokeWidth = Dp(value = 6F)
                )
            }

        }*//*

        val textState = remember { mutableStateOf(TextFieldValue("")) }
        SearchView(textState,navController,loginViewModel,listResponse11)
    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    state: MutableState<TextFieldValue>,
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel? = null,
    listResponse11: List<RouteListResponse>?,) {
    val context = LocalContext.current
    val store = UserStore(context)
    val tokenText = store.getAccessToken.collectAsState(initial = "")
    val tokenValue = remember {
        mutableStateOf(TextFieldValue())
    }
    val isLoading by remember { mutableStateOf(true) }

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
                shape = RoundedCornerShape(12.dp)
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
                            TextFieldValue("")
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
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            containerColor = Color.White,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )

//    val messages: List<String> = listOf("HAI", "Hello", "AAA")
//    var messages: List<RouteListResponse> = loginViewModel?.listResponse ?: emptyList()
    var selectedIndex by remember { mutableStateOf(-1) }
    val listState = rememberLazyListState()

     val messages = loginViewModel?.listResponse
//     var messages = listResponse11

    Log.e("DDDDD",loginViewModel?.listResponse.toString())
    Log.e("DDDDDFFFF message",messages.toString())
    Log.e("DDDDDFFFFEEEEE",listResponse11.toString())
//    if (isLoading) {

    if (messages==null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(104.dp)
                    .padding(20.dp),
                color = colorResource(id = R.color.purple_200),
                strokeWidth = Dp(value = 6F)
            )
        }

    }
    LazyColumn(state = listState) {
        items<RouteListResponse>(items = messages ?: emptyList()) {message ->
            Log.e("DriverselectRoute",messages?.size.toString())
            Text(
                text = message.name,
                modifier = Modifier
                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RectangleShape
                    )
                    .padding(16.dp)
                    .selectable(
                        selected = true,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                store.saveToken(tokenValue.value.text)
                            }
//                            loginViewModel?.updateRouteSelection(selectedIndex, "isChecked")
                            loginViewModel?.getDriverDetailsVM()
                            navController?.navigate(Routes.DriverDashboard.name)
                        },
                    ),
            )
        }
    }

}

*/

