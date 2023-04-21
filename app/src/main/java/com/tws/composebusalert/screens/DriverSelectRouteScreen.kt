package com.tws.composebusalert.screens

import android.inputmethodservice.Keyboard.Row
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
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
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private var route: String? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelectRouteScreen(
    navController: NavController,
    loginViewModel: DriverLoginViewModel,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreData(context)
    Log.e("DriverSelectRouteScreen", loginViewModel.listResponse.toString())
    LaunchedEffect(Unit) {
        loginViewModel.getRouteList("")
        Log.e("DriverSelectRouteScreen", loginViewModel.listResponse.toString())
        dataStore.screen("DashBoard Screen")
    }
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
//            .padding(top = 5.dp, start = 80.dp, bottom = 1.dp, end = 5.dp)
        ) {
         /*   Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TITLE",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Center)
//                        .padding(20.dp)
                )
                Button(
                    onClick = {
                        scope.launch {
                            if (route != null) {
                                dataStore.saverouteId(route!!)
                                navController.navigate(Routes.DriverDashboard.name)
                            } else {
                                Toast.makeText(context, "Please Select Route", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ), modifier = Modifier
                        .align(TopEnd)
                        .padding(
                            15.dp
                        )
                ) {
                    Text(text = "Save", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }*/
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(105.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text = "ROUTE LIST",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ClickableText(
                                text = AnnotatedString("Save"),

                                modifier = Modifier
                                    .align(TopEnd)
                                    .padding(
                                        15.dp
                                    ),
                                onClick = {
                                    scope.launch {
                                        if (route != null) {
                                            dataStore.saverouteId(route!!)
                                            navController.navigate(Routes.DriverDashboard.name)
                                        } else {
                                            Toast.makeText(context, "Please Select Route", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                })
                        }
                    }
                },
                modifier = Modifier.height(50.dp),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                )
            )
        }
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        SearchView(textState, loginViewModel, lifecycleOwner = lifecycleOwner, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    state: MutableState<TextFieldValue>,
    loginViewModel: DriverLoginViewModel? = null,
    lifecycleOwner: LifecycleOwner,
    navController: NavController,
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val dataStore = StoreData(context)
    val storedRouteId = dataStore.getrouteId.collectAsState(initial = "")
    val storedScreen = dataStore.getScreen.collectAsState(initial = "")
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 5.dp, 16.dp, 18.dp)
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
                        scope.launch {
                            dataStore.screen("DashBoard Screen")
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            loginViewModel?.getRouteList("")
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

    }
    Log.e("RouteID", storedRouteId.value)
    Log.e("storedScreen", storedScreen.value)

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
        var selectedIndex by remember { mutableStateOf(-1) }
        LazyColumn(state = listState) {
            items<RouteListResponse>(items = messages ?: emptyList()) { message ->
                Log.e("DriverselectRoute", messages.size.toString())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        /*  .background(
                              if (message == messages.getOrNull(selectedIndex)) {
                                  Color.Blue // Highlight the selected item
                              } else {
                                  Color.White
                              }
                          )*/
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RectangleShape
                        )
                        .padding(16.dp)
                        .clickable(
                            onClick = {
                                selectedIndex = messages.indexOf(message)
                                route = message.id
                                /* scope.launch {
                                    dataStore.saverouteId(message.id)
                                    dataStore.screen("DashBoard Screen")
                                }*/
                                Log.e("RouteID", storedRouteId.value)
                                Log.e("storedScreen", storedScreen.value)
                            },
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message.name,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = messages.indexOf(message) == selectedIndex,
                        onClick = {
                            selectedIndex = messages.indexOf(message)
                            route = message.id
                        }
                    )
                }
            }
        }
    }
}
