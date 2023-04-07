package com.tws.composebusalert.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
)
@Composable
fun DriverDashboard(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel?
) {
//    driverLoginViewModel?.getDriverDetailsVM()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))

    var bottomSheetText by remember { mutableStateOf("Initial bottom sheet text") }
    val coroutineScope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }

    ComposeBusAlertTheme {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFF03A9F4))
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val number = 18
                        val messages: List<String> = listOf("List1", "List2", "List3")
                        val listState = rememberLazyListState()
                        var selectedIndex by remember { mutableStateOf(-1) }
                        LazyColumn(state = listState) {
                            items(items = messages) { message ->
                                Text(
                                    text = message,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp, 1.dp, 15.dp, 1.dp)
//                    .clip(RoundedCornerShape(18.dp))
                                        .border(1.dp, Color.White, RectangleShape)
                                        .background(Color(0xFF03A9F4))
                                        .selectable(
                                            selected = true,
                                            onClick = {
                                                navController?.navigate(Routes.MapScreen.name) {
                                                    launchSingleTop = true
                                                }
                                            },
                                        ),
                                )
                            }
                        }
                        /* Text(
                             text = bottomSheetText,
                             fontSize = 20.sp,
                             color = Color.White,
                             textAlign = TextAlign.Left
                         )*/
                    }
                }
            },
            sheetPeekHeight = 0.dp,
            contentColor = Color.Red,
        ) {
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
                                        .align(CenterVertically)
                                )
                                //                        Spacer(modifier = Modifier.width(15.dp))
                                IconButton(
                                    onClick = { /*TODO*/ },
                                    modifier = Modifier.padding(70.dp, 0.dp, 2.dp, 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
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
                    RippleLoadingAnimation(
//                        navController = navController,
                        text = "Start",
                        box1 = 500.dp,
                        box2 = 130.dp,
                        box3 = 130.dp,
                        fontSize = 29.sp,
                        alignment = Center,
                        onClick = {

                            showDialog.value = true
//                    coroutineScope.launch {
//                    }
//                    navController?.navigate(Routes.AlertScreen.name)
                        })
                    if (showDialog.value) {
                        AlertDialog(shape = RoundedCornerShape(8.dp),
                            containerColor = Color.White,
                            modifier = Modifier.padding(5.dp),
                            onDismissRequest = { showDialog.value = false },
                            title = { Text(text = "ALERT") },
                            text = { Text(text = "Are you going to pickup or drop the students?") },
                            confirmButton = {
                                Button(onClick = {
                                    coroutineScope.launch {
//                                        bottomSheetText="Confirm"
                                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                            bottomSheetScaffoldState.bottomSheetState.expand()

                                        } else {
                                            bottomSheetScaffoldState.bottomSheetState.collapse()
                                        }
                                    }
                                    showDialog.value = false
                                }) {

                                    Text("PICKUP")
                                }
//                        TextButton(onClick = { showDialog11.value = false }) {

//                        }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        bottomSheetText = "Dismiss"
                                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                            bottomSheetScaffoldState.bottomSheetState.expand()

                                        } else {
                                            bottomSheetScaffoldState.bottomSheetState.collapse()
                                        }
                                    }
                                    showDialog.value = false
                                }) {
                                    Text("DROP")
                                }
//                        TextButton(onClick = { showDialog11.value = false }) {
//                        }
                            })

                    }
                    CardView(driverLoginViewModel)
                }

            }
        }

    }

}


@Composable
fun RippleLoadingAnimation(
    circleColor: Color = MaterialTheme.colorScheme.primary,
    animationDelay: Int = 1500,
//    navController: NavController? = null,
    text: String,
    box1: Dp,
    box2: Dp,
    box3: Dp,
    alignment: Alignment,
    fontSize: TextUnit,
    onClick: () -> Unit
) {

    val circles = listOf(remember {
        Animatable(initialValue = 0f)
    }, remember {
        Animatable(initialValue = 0f)
    }, remember {
        Animatable(initialValue = 0f)
    })

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / 3L) * (index + 1))
            animatable.animateTo(
                targetValue = 1f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay, easing = LinearEasing
                    ), repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment,
    ) {

        // outer circle and inner text
        Box(
//        contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.size(box1)
        ) {

            Box(
                modifier = Modifier
                    .size(size = box1)
                    .background(color = Color.Transparent)
                    .align(Center)
            ) {


                circles.forEachIndexed { index, animatable ->
                    Box(
                        modifier = Modifier
                            .scale(scale = animatable.value)
                            .size(size = 400.dp)
                            .clip(shape = CircleShape)
                            .background(
                                color = circleColor.copy(alpha = (1 - animatable.value))
                            )
                            .align(Center)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(size = box2)
                    .clip(shape = CircleShape)
                    .background(
                        color = circleColor
                    )
                    .align(Alignment.Center)
            ) {
                Button(
                    modifier = Modifier.size(box3),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
//                    containerColor = colorResource(id = R.color.purple_500),
                        containerColor = circleColor, contentColor = Color.White
                    ),
                    onClick = onClick,
                ) {

                }
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    modifier = Modifier.align(Center),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }

    }

}


@Composable
fun CardView(driverLoginViewModel: DriverLoginViewModel?) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Yellow),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 65.dp, 16.dp, 2.dp),
//            .background(Color.Green),

    ) {

//        val name=  driverLoginViewModel?.firstName?.value.toString()
        val name=  driverLoginViewModel?.firstName?.value.toString()
//        val address=  driverLoginViewModel?.address?.value.toString()
        Surface(
//            contentColor = colors.contentColor(enabled = true).value,
//            contentColor = Color.Red,
//            modifier = Modifier.background(Color.Green),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onTertiary)
                    .padding(8.dp),
            ) {
                Column(Modifier.padding(2.dp)) {
                    Text(
                        "Driver Name",
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
                        name,
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
                        "address",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                }
                Spacer(modifier = Modifier.width(80.dp))
                Image(
                    painter = painterResource(id = R.drawable.img_5),
                    contentDescription = "My Image",
                    contentScale = ContentScale.Crop
                )
            }
        }

    }
}





