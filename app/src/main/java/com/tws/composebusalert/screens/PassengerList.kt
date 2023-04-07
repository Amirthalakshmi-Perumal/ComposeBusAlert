package com.tws.composebusalert.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerList(navController: NavController? = null) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(105.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        //                        Spacer(modifier = Modifier.width(55.dp))
                        Text(
                            text = "DASHBOARD",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                //                                .fillMaxWidth()
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        //                        Spacer(modifier = Modifier.width(15.dp))
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.padding(70.dp, 0.dp, 2.dp, 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                tint = Color.White,

                                )
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
        CardList()
    }
}


@Composable
fun CardViewStudent() {
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

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Yellow),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 18.dp, 16.dp, 2.dp),
//            .background(Color.Green),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onTertiary)
                .padding(8.dp),
        ) {
            Column(Modifier.padding(2.dp)) {
                Text(
                    "Student Name",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,

                    )
                Text(
                    "No of Students",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,

                    )
                Text(
                    "Bus Routes",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,

                    )
                Text(
                    "No of Students",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,

                    )
                Text(
                    "Bus Routes",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,

                    )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(Modifier.padding(2.dp)) {
                Text(
                    "xyz",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
                Text(
                    "35",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
                Text(
                    "",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
                Text(
                    "35",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
                Text(
                    "",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
            }
            Spacer(modifier = Modifier.width(80.dp))
            Column() {
                IconButton(
                    onClick = {
                        showDialog.value = true
//                        showDialog1 = true
                    }, modifier = Modifier.padding(80.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color.Black,
                    )
                }
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
                                MainScreen()
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog.value = false

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
                Image(
                    painter = painterResource(id = R.drawable.student),
                    contentDescription = "My Image",
                    contentScale = ContentScale.Crop
                )
            }

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
fun CardList() {
    /* val items = listOf(
         "Item 1",
         "Item 2",
         "Item 3",
         "Item 4",
         "Item 5",
     )*/
    val items = 15

    LazyColumn(
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(items) { item ->
            CardViewStudent()
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
fun MainScreen() {
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


}
