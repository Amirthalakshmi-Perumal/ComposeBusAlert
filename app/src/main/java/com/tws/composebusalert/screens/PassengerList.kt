package com.tws.composebusalert.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.responses.PassengerDetailResponse
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerList(
    navController: NavController? = null,
    loginViewModel: DriverLoginViewModel? = null, lifecycleOwner: LifecycleOwner,
) {
    val context = LocalContext.current
    var studentList: List<PassengerDetailResponse>? = null
//    var studentList = loginViewModel?.getPassengersDetail(context)
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        loginViewModel?.getPassengersDetail(context)

    }

    val passengerDetails = loginViewModel?.res?.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
//                                    .padding(105.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "DASHBOARD",
                                textAlign = TextAlign.Center,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.Center)
                            )

                            IconButton(
                                onClick = {
//                                    showExitDialog.value=true

                                },
                                modifier = Modifier.align(TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,

                                    )
                            }
                        }

                    }
                },
                modifier = Modifier.height(50.dp),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = Color(0xFF03A9F4),
                )
            )
        }
        CreateView(passenderList = passengerDetails?.value)

    }


}

@Composable
fun CreateView(
    passenderList: List<PassengerDetailResponse>?,
) {
    if (passenderList == null) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(104.dp)
                .padding(20.dp),
            color = colorResource(id = R.color.purple_200),
            strokeWidth = Dp(value = 6F)
        )

    } else {
//        Log.e("TTTT", passenderList.toString())
        Log.e("passengerDetails", passenderList.toString())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            CardList(passenderList)
        }
    }
}


@Composable
fun Summa() {

    var expanded by remember { mutableStateOf(false) }
//    val options = listOf("Option 1", "Option 2", "Option 3")
    val options = listOf(
        "2 mins",
        "5 mins",
        "10 mins",
        "15 mins",
        "20 mins",
        "25 mins",
        "30 mins",
        "35 mins",
        "40 mins",
        "45 mins",
        "50 mins",
        "55 mins",
        "60 mins",
    )
    var selectedOption by remember { mutableStateOf(options[0]) }


    AlertDialog(
        onDismissRequest = {
            expanded = false
        },
//        title = { Text("Select an op") },
        title = {
            Text(
                text = "ENTER PASSENGER CODE", fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        },

        text = {
            Row {
                Text(text = "Notify before", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(75.dp))
                Box {
                    Text(text = selectedOption,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .align(TopStart)
//                        .padding(26.dp)
                    )

                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .padding(6.dp, 0.dp, 5.dp, 0.dp)
                            .align(TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand",
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(text = option)
                            }, onClick = {
                                selectedOption = option
                                expanded = false
                            })
                        }
                    }
                }
            }


        },
        confirmButton = {
            Button(
                onClick = {

                    // Do something with the selected option
                },
                modifier = Modifier.fillMaxWidth(),
//                shape = RectangleShape,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Yellow),
            ) {
                Text("SUBMIT", textAlign = TextAlign.Center)
            }
        },
        shape = RoundedCornerShape(14.dp),

        )


}


@Composable
fun CardList(studentList: List<PassengerDetailResponse>?) {
    /* val items = listOf(
         "Item 1",
         "Item 2",
         "Item 3",
         "Item 4",
         "Item 5",
     )*/
    val items = studentList?.size
    if (items != null) {
        LazyColumn(
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(items) { item ->
                CardViewStudent(studentList, item)
            }
        }
    }
}

@Composable
fun CardViewStudent(studentList: List<PassengerDetailResponse>, index: Int) {
    val showDialog = remember { mutableStateOf(false) }
    var showDialog1 by remember { mutableStateOf(false) }
//    val options = listOf("Option 1", "Option 2", "Option 3")
//    val selectedOption = remember { mutableStateOf(options[0]) }

    var expanded by remember { mutableStateOf(false) }
//    val options = listOf("Option 1", "Option 2", "Option 3")
    /* val options = listOf(
         "2 mins",
         "5 mins",
         "10 mins",
         "15 mins",
         "20 mins",
         "25 mins",
         "30 mins",
         "35 mins",
         "40 mins",
         "45 mins",
         "50 mins",
         "55 mins",
         "60 mins",
     )
     var selectedOption by remember { mutableStateOf(options[0]) }*/
    var color = remember {
        mutableStateOf(Color(0xFFFFE5B4))
    }
 /* if(studentList[index].notificationDetails?.size==0){
        color.value=Color(0xFF84D560)
    }else{
        color.value=Color(0xFFFFE5B4)
    }*/

    var mins = remember {
        mutableStateOf(
            if(studentList[index].notificationDetails?.size==0){
                color.value=Color(0xFF84D560)
              "0 mins"
            }else{
                studentList[index].notificationDetails?.get(0)?.notifyTime.toString() + " mins"
            }

        )
    }
    /* if(studentList[index].notificationDetails?.get(0)?.notifyTime==null){
    //            studentList[index].notificationDetails?.get(0)?.notifyTime.toString() + " mins"
        Color(0xFF84D560)
    }else{
        Color(0xFFFFE5B4)
    }*/
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Yellow),
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp, 18.dp, 16.dp, 2.dp),
            .padding(18.dp).clickable {

            },
        colors = CardDefaults.cardColors(containerColor = color.value)
//            .background(Color.Green),

    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
//                .background(MaterialTheme.colorScheme.onTertiary)
                    .padding(12.dp),

//            verticalAlignment = Alignment.CenterVertically
            )
            {
                Column(Modifier.padding(2.dp)) {
                    Text(
                        "Name",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Class",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Section",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Route",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Pickup Stop",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Drop Stop",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )
                    Text(
                        "Notify Time",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,

                        )

                }
//            Spacer(modifier = Modifier.width(20.dp))
                Column(Modifier.padding(2.dp)) {
                    Text(
                        studentList[index].name ?: "Dummy Name",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        studentList[index].extraData?.standard ?: "Dummy Name",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        studentList[index].extraData?.section ?: "Dummy Name",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        studentList[index].route?.get(0)?.name ?: "Dummy route",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        studentList[index].pickupStopping?.name ?: "Dummy pickupStopping",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        studentList[index].dropStopping?.name ?: "Dummy dropStopping",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    Text(
                        mins.value,
//                    studentList[index].notificationDetails?.get(0)?.notifyTime.toString() + " mins"
//                    studentList[index].notificationDetails?.get(index-1)?.notifyTime.toString(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                }
//            Spacer(modifier = Modifier.width(10.dp))
                Box(
                    Modifier
                        .padding(2.dp)
//                        .padding(start = 19.dp)
//                .align(Bottom)
                ) {
                    if (showDialog.value) {
//                    AlertDialogWithDropdownList()
//                    Summa()
                        AlertDialog(
                            onDismissRequest = {
                                showDialog.value = false
                            },
//        title = { Text("Select an op") },
                            title = {
                                Text(
                                    text = "ENTER PASSENGER CODE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },

                            text = {
                                Row {
                                    Text(text = "Notify before", fontWeight = FontWeight.Bold)
                                    mins.value = MainScreen()
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showDialog.value = false
                                        color.value = Color(0xFFFFE5B4)
//                                    mins.value="Sub"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
//                shape = RectangleShape,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color.Transparent),
                                ) {
                                    Text("SUBMIT", textAlign = TextAlign.Center)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    /*   Image(
       //                        painter = rememberAsyncImagePainter(
       //                            "https://picsum.photos/id/237/200/300"
       //                        ),
                           painter = painterResource(id = R.drawable.img_1),
                           contentDescription = "My Image",
                           contentScale = ContentScale.Fit,
                           modifier = Modifier.height(150.dp).width(200.dp).align(CenterEnd)
       //                        .size(130.dp)
       //                        .padding(18.dp)
                       )*/
                    Image(
                        painter = painterResource(id = R.drawable.img_1),
                        contentDescription = "My Image",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .height(150.dp)
                            .width(200.dp)
//                        .size(130.dp)
//                        .padding(18.dp)
                    )
                }
            }
            IconButton(
                onClick = {
                    showDialog.value = true
//                        showDialog1 = true
                }, modifier = Modifier.align(TopEnd)
//                        .padding(0.dp, 0.dp,0.dp, 120.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = Color.Black,
                )
            }

        }

    }
}

/*
@Composable
fun DropdownList(options: List<String>, selectedOption: MutableState<String>) {
    var a by remember {
        mutableStateOf(false)
    }

    Row {
        Text(text = "Notify before", fontWeight = FontWeight.Bold)
        MainScreen()

        */
/*  IconButton(
              onClick = {
                  a = true
              }, modifier = Modifier.padding(120.dp, 0.dp, 0.dp, 0.dp)
          ) {
              Icon(
                  imageVector = Icons.Filled.ArrowDropDown,
                  contentDescription = null,
                  tint = Color.Black,
              )
          }*//*

    }
//    if (a) {
//        DropdownMenu(
//            expanded = a, onDismissRequest = { a = false }, modifier = Modifier.width(120.dp)
//        ) {
//
//            options.forEach { option ->
//                DropdownMenuItem(
////                    modifier = Modifier.align(End),
//                    onClick = {
//                        a = false
//                        selectedOption.value = option
//                    },
//                    text = { Text(option) },
//                )
//            }
//        }
//    }


}
*/
/*

@Composable
fun AlertDialogWithDropdownList() {
    val options = listOf(
        "5 mins",
        "10 mins",
        "15 mins",
//        "20 mins",
//        "25 mins",
//        "30 mins",
//        "35 mins",
//        "40 mins",
//        "45 mins",
//        "50 mins",
//        "55 mins",
//        "60 mins",
    )
    val selectedOption = remember { mutableStateOf(options[0]) }
    AlertDialog(
        onDismissRequest = {

        },
//        title = { Text("Select an op") },
        title = {
            Text(
                text = "ENTER PASSENGER CODE", fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        },

        text = { DropdownList(options, selectedOption) },
        confirmButton = {
            Button(
                onClick = {

                    // Do something with the selected option
                },
                modifier = Modifier.fillMaxWidth(),
//                shape = RectangleShape,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Yellow),
            ) {
                Text("SUBMIT", textAlign = TextAlign.Center)
            }
        },
        shape = RoundedCornerShape(14.dp),

        )
}
*/

/*

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyUI() {
    val listItems = arrayOf("Favorites", "Options", "Settings", "Share")
    val contextForToast = LocalContext.current.applicationContext

    // state of the menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // remember the selected item
    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    // box
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        // text field
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        // menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // this is a column scope
            // all the items are added vertically
            listItems.forEach { selectedOption ->
                // menu item
                DropdownMenuItem(
                    text = {
                        Text(text = selectedOption)
                    }
                    ,onClick = {
                    selectedItem = selectedOption
                    Toast.makeText(contextForToast, selectedOption, Toast.LENGTH_SHORT).show()
                    expanded = false
                })
            }
        }
    }
}
*/


@Composable
fun MainScreen(): String {
    val list = listOf(
        "2 mins",
        "5 mins",
        "10 mins",
        "15 mins",
        "20 mins",
        "25 mins",
        "30 mins",
        "35 mins",
        "40 mins",
        "45 mins",
        "50 mins",
        "55 mins",
        "60 mins",
    )
//    val list = listOf("one", "two", "three", "four", "five")
    val expanded = remember { mutableStateOf(false) }
    val currentValue = remember { mutableStateOf(list[0]) }

    val scrollState = rememberScrollState()
    var offset by remember { mutableStateOf(0f) }


//    Surface(modifier = Modifier.fillMaxSize()) {

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {

        Row(modifier = Modifier
            .clickable {
                expanded.value = !expanded.value
            }
            .align(TopEnd)
            /*.scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            )*/
        ) {
            Text(text = currentValue.value)
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            DropdownMenu(expanded = expanded.value, onDismissRequest = {
                expanded.value = false
            }) {

                Column(
                    modifier = Modifier
                        .height(250.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    list.forEach {
                        DropdownMenuItem(
                            onClick = {
                                currentValue.value = it
                                expanded.value = false
                            },
//                        modifier = Modifier.verticalScroll(rememberScrollState()),
                            text = {
                                Text(text = it)
                            })
                    }
                }
            }
        }
    }
//    }

    return currentValue.value
}
