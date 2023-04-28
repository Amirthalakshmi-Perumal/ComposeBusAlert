package com.tws.composebusalert.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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


//private var routeId: String? = null
private var routeName: String? = null
private var pickId: String? = null
private var dropId: String? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelectRouteScreen(
    navController: NavController,
    loginViewModel: DriverLoginViewModel,
    lifecycleOwner: LifecycleOwner,
    argValue: String
) {
    val activity = (LocalContext.current as Activity)

    BackHandler(true) {
        if (argValue == "OTP") {
            activity.finish()
        } else {
            navController.navigate(Routes.DriverDashboard.name)
        }
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dataStore = StoreData(context)
    val storedRouteId = dataStore.getrouteId.collectAsState(initial = "")
    val storedRouteName = dataStore.getRouteName.collectAsState(initial = "")
    val storedPickUpId = dataStore.getPickUpId.collectAsState(initial = "")
    val storedDropId = dataStore.getDropId.collectAsState(initial = "")

    Log.e("DriverSelectRouteScreen", loginViewModel.listResponse.toString())
    LaunchedEffect(Unit) {
        loginViewModel.getRouteList("")
        Log.e("DriverSelectRouteScreen", loginViewModel.listResponse.toString())
        dataStore.saveScreen("DashBoard Screen")
    }
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
//            .padding(top = 5.dp, start = 80.dp, bottom = 1.dp, end = 5.dp)
        ) {
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
                            ClickableText(text = AnnotatedString("Save"),
                                modifier = Modifier
                                    .align(TopEnd)
                                    .padding(
                                        15.dp
                                    ),
                                onClick = {
                                    scope.launch {
                                        if (routeName != null) {
                                            navController.navigate(Routes.DriverDashboard.name) {
                                                navController.popBackStack()
                                            }
                                            dataStore.saveRouteName(routeName!!)
                                            Log.e("HHHH", "routeName " + routeName.toString())
                                            Log.e(
                                                "HHHHstoredRouteName",
                                                "storedRouteName  " + storedRouteName.value
                                            )
                                          /*  if (routeId != null) {
                                                Log.e(
                                                    "HHHH",
                                                    "storedRouteId  var  " + routeId.toString()
                                                )
                                                dataStore.saverouteId(routeId!!)
                                                Log.e(
                                                    "HHHHRouteID",
                                                    "storedRouteId  " + storedRouteId.value
                                                )
                                            }*/
                                            if (pickId != null) {
                                                dataStore.savePickUpId(pickId!!)
                                                Log.e(
                                                    "HHHHstoredPickUpId",
                                                    "storedPickUpId" + storedPickUpId.value
                                                )

                                            }
                                            if (dropId != null) {
                                                dataStore.saveDropId(dropId!!)
                                                Log.e(
                                                    "HHHHstoredDropId",
                                                    "storedDropId" + storedDropId.value
                                                )

                                            }

                                        } else {
                                            routeName = storedRouteName.value
                                            pickId = storedPickUpId.value
                                            dropId = storedDropId.value
                                            Log.d("DDDDDD","storedRouteName "+storedRouteName.value+"routeName "+routeName)
                                            navController.navigate(Routes.DriverDashboard.name) {
                                                navController.popBackStack()
                                            }
                                              /*if (storedRouteId.value != "") {
//                                                  routeId = storedRouteId.value
                                                  routeName = storedRouteName.value
                                                  pickId = storedPickUpId.value
                                                  dropId = storedDropId.value
                                                  navController.navigate(Routes.DriverDashboard.name) {
                                                      navController.popBackStack()
                                                  }
                                              } else {
                                                  Toast.makeText(
                                                      context,
                                                      "Please Select Route",
                                                      Toast.LENGTH_SHORT
                                                  )
                                                      .show()
                                              }*/
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
        RouteListView(textState, loginViewModel, lifecycleOwner = lifecycleOwner, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListView(
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

    val storedRouteName = dataStore.getRouteName.collectAsState(initial = "")
    val storedPickUpId = dataStore.getPickUpId.collectAsState(initial = "")
    val storedDropId = dataStore.getDropId.collectAsState(initial = "")

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
                            dataStore.saveScreen("DashBoard Screen")
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
    Log.e("storedRouteName", "storedRouteName  " + storedRouteName.value)
    Log.e("storedPickUpId", "storedPickUpId" + storedPickUpId.value)
    Log.e("storedDropId", "storedDropId" + storedDropId.value)

    if (loginViewModel?.getRouteList("") != null) {
        messages = loginViewModel.getRouteList("")
    }
    val routNameList: List<String>? = messages?.map { it.name }


    val routess = routNameList?.distinct()
    val routeList: MutableList<RouteListResponse> = mutableListOf()
    routess?.forEach {
        routeList.add(RouteListResponse(id = "", name = it, type = ""))
    }
    Log.e("TTYYT", routeList.size.toString())

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
            items<RouteListResponse>(items = routeList) { listitem ->
                Log.e("TTYYT7657645", routeList.size.toString())
                Log.e("DriverselectRoute", messages.size.toString())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                          .background(
                              if (listitem == messages.getOrNull(selectedIndex)) {
                                  Color.Red // Highlight the selected item
                              } else {
                                  Color.White
                              }
                          )
                        .padding(16.dp, 0.dp, 16.dp, 0.dp)
                        .border(
                            width = 1.dp, color = Color.LightGray, shape = RectangleShape
                        )
                        .padding(16.dp)
                        .clickable(
                            onClick = {
                                selectedIndex = messages.indexOf(listitem)
                                    for (i in messages.indices){
                                        if(listitem.name==messages[i].name){
                                            if (messages[i].type == "Pickup") {
                                                pickId = messages[i].id
                                            }
                                            if (messages[i].type == "Drop") {
                                                dropId = messages[i].id
                                            }
                                            routeName = messages[i].name
                                        }
                                    }
                                /*routeId = listitem.id

                                pickId = listitem.type

                                if (listitem.type == "Pickup") {
                                    pick = listitem.type
                                }
                                if (listitem.type == "Drop") {
                                    drop = listitem.type
                                }
                                for (i in messages.indices){
                                    if(routeName==messages[i].name){
                                        if (messages[i].type == "Pickup") {
                                            pick = listitem.type
                                        }
                                        if (messages[i].type == "Drop") {
                                            drop = listitem.type
                                        }
                                    }
                                }*/


                                scope.launch {
//                                    dataStore.saverouteId(message.id)
//                                     dataStore.saveDriverName()
                                    dataStore.saveScreen("DashBoard Screen")
                                }
                                Log.e("RouteID", storedRouteId.value)
                                Log.e("storedScreen", storedScreen.value)
                                Log.e("storedRouteName", "storedRouteName" + storedRouteName.value)
                                Log.e("storedPickUpId", "storedPickUpId" + storedPickUpId.value)
                                Log.e("storedDropId", "storedDropId" + storedDropId.value)
                            },
                        ), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = listitem.name, modifier = Modifier.weight(1f)
                    )
                    RadioButton(selected = messages.indexOf(listitem) == selectedIndex, onClick = {
                        selectedIndex = messages.indexOf(listitem)
//                        routeId = listitem.id
                        for (i in messages.indices){
                            if(listitem.name==messages[i].name){
                                if (messages[i].type == "Pickup") {
                                    pickId = messages[i].id
                                }
                                if (messages[i].type == "Drop") {
                                    dropId = messages[i].id
                                }
                                routeName = messages[i].name
                            }
                        }
                        /*for (i in messages.indices){
                            if(routeName==messages[i].name){
                                if (messages[i].type == "Pickup") {
                                    pick = listitem.type
                                }
                                if (messages[i].type == "Drop") {
                                    drop = listitem.type
                                }
                            }
                        }*/

                    })
                }
            }
        }
    }
}
