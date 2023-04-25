package com.tws.composebusalert.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.responses.VehicleRouteItem
import com.tws.composebusalert.responses.VehicleRouteListResponse
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.core.os.bundleOf
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

var vehicleList: ArrayList<VehicleRouteItem>? = null
var listtt: VehicleRouteListResponse? = null

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
)
@Composable
fun DriverDashboard(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel?,
    lifecycleOwner: LifecycleOwner
) {

    vehicleList =driverLoginViewModel?.getVehicleList("")
    Log.e("TTT", "TTT  "+vehicleList.toString())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreData(context)
    val storedScreen = dataStore.getScreen.collectAsState(initial = "")
    val storedRoute = dataStore.getrouteId.collectAsState(initial = "")
//    BackHandler(true) {
//
//        Log.d("TAG", "OnBackPressed")
////        (context as? Activity)?.finish()
//        (context as? Activity)?.let { activity ->
//            activity.finish()
//        }
//        Log.d("TAGFFF", "sddfszdgdfzbzOnBackPressed")
//
//    }
//    val lifecycleOwner = LocalLifecycleOwner.current

    val onBack: () -> Unit = {
        Log.d("TAG", "OnBackPressed")
        (context as? Activity)?.let { activity ->
            activity.finish()
        }
    }

//    BackHandler(onBack)
    /*   LaunchedEffect(key1 = Unit) {
           delay(8000L)
           val a  = driverLoginViewModel?.getVehicleList("")
           Log.e("", "Launch block")
           Log.e("", "Launch block $a")
           vehicleList=a
           Log.e("vehicleListDrivergetVehicleList", vehicleList.toString())
           Log.e("Routeeeeee", storedRoute.value.toString())
           Log.e("Screen", storedScreen.value)
       }*/
    LaunchedEffect(Unit) {
        dataStore.screen("DashBoard Screen")
//       var a= driverLoginViewModel?.getVehicleList("")
//        if (driverLoginViewModel?.getVehicleList("") != null) {
//            vehicleList =a
//        }
//        vehicleList=driverLoginViewModel?.getVehicleList("")
        Log.e("DrivergetVehicleList", driverLoginViewModel?.listResponseVehicle.toString())
        Log.e("vehicleListDrivergetVehicleList", vehicleList.toString())
        Log.e("Routeeeeee", storedRoute.value.toString())
        Log.e("Screen", storedScreen.value)
    }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    var bottomSheetText by remember { mutableStateOf("Initial bottom sheet text") }
    val coroutineScope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }

    var messages: VehicleRouteListResponse? = null

    ComposeBusAlertTheme {

        var lastBackPressedTime: Long = 0

        val activity = LocalContext.current as Activity

        BackHandler(enabled = true) {
            val currentBackPressedTime = System.currentTimeMillis()
            if (currentBackPressedTime - lastBackPressedTime <= 2000) {
                activity.finish()
            } else {
                Toast.makeText(activity, "Press back again to exit", Toast.LENGTH_SHORT).show()
                lastBackPressedTime = currentBackPressedTime
            }
        }

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
                        val settingResultRequest = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult()
                        ) { activityResult ->
                            if (activityResult.resultCode == ComponentActivity.RESULT_OK) Log.d(
                                "appDebug", "Accepted"
                            )
                            else {
                                Log.d("appDebug", "Denied")
                            }
                        }
                        val number = 18

                        val listState = rememberLazyListState()
                        val permissions = arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        val launcherMultiplePermissions = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestMultiplePermissions()
                        ) { permissionsMap ->
                            val areGranted =
                                permissionsMap.values.reduce { acc, next -> acc && next }
                            if (areGranted) {
                                driverLoginViewModel?.locationRequired = true
                                driverLoginViewModel?.startLocationUpdates()
                                Toast.makeText(
                                    context, "Permission Granted", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        var selectedIndex by remember { mutableStateOf(-1) }
//                        VehicleRouteItem
                        /*      if (messages != null) {
                                  Log.e("VehicleRouteItem messages.toString()", messages.toString())

                                  var selectedIndex by remember { mutableStateOf(-1) }
                                  LazyColumn(state = listState) {
                                      items<VehicleRouteListResponse>(items = messages!!) { message ->
                                          Log.e("VehicleRouteItem messages.size.toString()", messages!!.size.toString())
                                          Log.e("VehicleRouteItem messages.toString()", messages.toString())
                                          Log.e("VehicleRouteItem message.toString()", message.toString())
                                          Log.e("VehicleRouteItem message.vehicle.toString()", message.vehicle.toString())

                                          *//* Text(
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
                                                     driverLoginViewModel?.checkLocationSetting(
                                                         context = context,
                                                         onDisabled = { intentSenderRequest ->
                                                             settingResultRequest.launch(
                                                                 intentSenderRequest
                                                             )
                                                         },
                                                         onEnabled = {
                                                             if (permissions.all {
                                                                     ContextCompat.checkSelfPermission(
                                                                         context, it
                                                                     ) == PackageManager.PERMISSION_GRANTED
                                                                 }) {
                                                                 driverLoginViewModel.startLocationUpdates()
                                                             } else {
                                                                 launcherMultiplePermissions.launch(
                                                                     permissions
                                                                 )
                                                             }
                                                         })

                                                 },
                                             ),
                                     )*//*

                                }
                            }
                        }
                        if (vehicleList != null) {
                            Log.e("VehicleRouteItem messages.toString()", vehicleList.toString())

                            var selectedIndex by remember { mutableStateOf(-1) }
                            LazyColumn(state = listState) {
                                items<VehicleRouteListResponse>(items = vehicleList!!) { message ->
                                    Log.e("VehicleRouteItem messages.size.toString()", vehicleList!!.size.toString())
                                    Log.e("VehicleRouteItem messages.toString()", vehicleList.toString())
                                    Log.e("VehicleRouteItem message.toString()", vehicleList.toString())
                                    Log.e("VehicleRouteItem message.vehicle.toString()", vehicleList.toString())

                                    *//* Text(
                                         text = message.,
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
                                                     driverLoginViewModel?.checkLocationSetting(
                                                         context = context,
                                                         onDisabled = { intentSenderRequest ->
                                                             settingResultRequest.launch(
                                                                 intentSenderRequest
                                                             )
                                                         },
                                                         onEnabled = {
                                                             if (permissions.all {
                                                                     ContextCompat.checkSelfPermission(
                                                                         context, it
                                                                     ) == PackageManager.PERMISSION_GRANTED
                                                                 }) {
                                                                 driverLoginViewModel.startLocationUpdates()
                                                             } else {
                                                                 launcherMultiplePermissions.launch(
                                                                     permissions
                                                                 )
                                                             }
                                                         })

                                                 },
                                             ),
                                     )*//*

                                }
                            }
                        }*/

                        if (vehicleList != null) {
                            var selectedIndex by remember { mutableStateOf(-1) }

                            Log.e("VehicleRouteItem messages.toString()", "VehicleRouteItem messages.toString()"+vehicleList.toString())
                            LazyColumn {
                                items(
                                    vehicleList?.toList() ?: emptyList()
                                ) { vehicleLists ->
                                    // Add your composable item here
                                    Text(
                                        text = vehicleLists.vehicle[0].name.toString(),
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
                                                    selectedIndex =
                                                        vehicleList!!.indexOf(vehicleLists)

                                                    navController?.navigate(Routes.MapScreen.name) {

                                                        launchSingleTop = true
                                                    }
                                                    driverLoginViewModel?.checkLocationSetting(
                                                        context = context,
                                                        onDisabled = { intentSenderRequest ->
                                                            settingResultRequest.launch(
                                                                intentSenderRequest
                                                            )
                                                        },
                                                        onEnabled = {
                                                            if (permissions.all {
                                                                    ContextCompat.checkSelfPermission(
                                                                        context, it
                                                                    ) == PackageManager.PERMISSION_GRANTED
                                                                }) {
                                                                driverLoginViewModel.startLocationUpdates()
                                                            } else {
                                                                launcherMultiplePermissions.launch(
                                                                    permissions
                                                                )
                                                            }
                                                        })

                                                },
                                            ),
                                    )

                                }
                            }

                        } else {
//                            vehicleList=driverLoginViewModel?.getVehicleList("")
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(104.dp)
                                        .padding(20.dp),
                                    color = colorResource(id = R.color.teal_200),
                                    strokeWidth = Dp(value = 6F)
                                )
                            }
                        }
                        /*     LazyColumn {
                                 items(vehicleList?.toList() ?: emptyList()) { vehicleList ->
                                     // Add your composable item here
                                     Text(
                                         text = vehicleList.type.toString(),
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
                                                     driverLoginViewModel?.checkLocationSetting(
                                                         context = context,
                                                         onDisabled = { intentSenderRequest ->
                                                             settingResultRequest.launch(
                                                                 intentSenderRequest
                                                             )
                                                         },
                                                         onEnabled = {
                                                             if (permissions.all {
                                                                     ContextCompat.checkSelfPermission(
                                                                         context, it
                                                                     ) == PackageManager.PERMISSION_GRANTED
                                                                 }) {
                                                                 driverLoginViewModel.startLocationUpdates()
                                                             } else {
                                                                 launcherMultiplePermissions.launch(
                                                                     permissions
                                                                 )
                                                             }
                                                         })

                                                 },
                                             ),
                                     )

                                 }
                             }*/
                        /*   if (vehicleList == null) {
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

                           }*/
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
//                                    .padding(105.dp, 0.dp, 0.dp, 0.dp)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "DASHBOARD",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .align(Center)
                                    )
                                    IconButton(
                                        onClick = {
                                            navController?.navigate("A/DRIVER")
//                                            navController?.navigate(Routes.DriverSelectRouteScreen.name)
                                        },
                                        modifier = Modifier
                                            .padding(end = 35.dp)
                                            .align(TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Settings,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    IconButton(
                                        onClick = {

                                            scope.launch {
                                                driverLoginViewModel?.signOut(
                                                    navController,
                                                    context
                                                )
                                            }
                                            navController?.navigate(Routes.Dashboard.name)
                                        },
                                        modifier = Modifier.align(TopEnd)
                                    ) {
                                        Icon(
//                                        imageVector =  R.drawable.logout as ImageVector,
                                            painter = painterResource(id = R.drawable.logout),
                                            contentDescription = null,
                                            tint = Color.White,

                                            )
                                    }
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
                            CoroutineScope(Dispatchers.IO).launch {
//                                vehicleList =driverLoginViewModel?.getVehicleList("")
                                listtt = driverLoginViewModel?.getVehicleList("")
                                Log.e("AMIRTHA RippleButton ", vehicleList.toString())
                                Log.e("AMIRTHA RippleButton listtt ", listtt.toString())
                            }
                            /* coroutineScope.launch {
                                 vehicleList = driverLoginViewModel?.getVehicleList("")
                                 Log.e("AMIRTHA RippleButton ", vehicleList.toString())
                             }*/
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
//                                            CoroutineScope(Dispatchers.IO).launch {
//                                                driverLoginViewModel?.getVehicleList("")
//                                                vehicleList=driverLoginViewModel?.getVehicleList("")
                                            Log.e("Vehicle ", vehicleList.toString())
//                                            }
//                                            if (driverLoginViewModel?.getVehicleList("") != null) {
//                                                messages = driverLoginViewModel.getVehicleList("")
//                                                vehicleList =driverLoginViewModel.getVehicleList("")
//                                            }

                                            bottomSheetScaffoldState.bottomSheetState.expand()

                                        } else {

//                                            if (driverLoginViewModel?.getVehicleList("") != null) {
//                                                messages = driverLoginViewModel.getVehicleList("")
//                                                vehicleList =driverLoginViewModel.getVehicleList("")
//                                            }
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
//                                            CoroutineScope(Dispatchers.IO).launch {
//                                                driverLoginViewModel?.getVehicleList("")
////                                                vehicleList=driverLoginViewModel?.getVehicleList("")
//                                                Log.e("Vehicle ", vehicleList.toString())
//                                            }
//                                            if (driverLoginViewModel?.getVehicleList("") != null) {
//                                                messages = driverLoginViewModel.getVehicleList("")
////                                                vehicleList = driverLoginViewModel.getVehicleList("")
//                                            }
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
                    CardView(driverLoginViewModel, lifecycleOwner)
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
fun CardView(driverLoginViewModel: DriverLoginViewModel?, lifecycleOwner: LifecycleOwner) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Yellow),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 65.dp, 16.dp, 2.dp),
//            .background(Color.Green),

    ) {
        val context = LocalContext.current
        val dataStore = StoreData(context)
        val storedDriverName = dataStore.getDriverName.collectAsState(initial = "")
        val storedImage = dataStore.getImageUrl.collectAsState(initial = "")
        val storedAddress = dataStore.getAddress.collectAsState(initial = "")

//        val name=  driverLoginViewModel?.firstName?.value.toString()
        val name = driverLoginViewModel?.firstName?.value.toString()
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
                        storedDriverName.value.toString(),
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
                        storedAddress.value ?: "kjhnkjmb",
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                }
                Spacer(modifier = Modifier.width(60.dp))
                androidx.compose.material.IconButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
//                        driverLoginViewModel?.getVehicleList("")
//                        vehicleList=driverLoginViewModel?.getVehicleList("")
                        Log.e("AMIRTHA", vehicleList.toString())
                        Log.e("AMIRTHA LL", vehicleList?.get(0)?.startPoint?.latitude.toString())
                    }

                }, modifier = Modifier.align(CenterVertically)) {
                    Log.e("LAK",storedImage.value.toString())
                    Log.e("LAK",storedDriverName.value.toString())
                    Log.e("LAK",storedAddress.value.toString())

//                    if(storedImage.value==null || storedImage.value.toString()==" "){
                        Image(
                            painter = rememberAsyncImagePainter(
                                "https://picsum.photos/id/237/200/300"),
                            contentDescription = "Driver image",contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp)
                        )

//                    }else{
                      /*  Image(
                            painter = rememberAsyncImagePainter(
                                storedImage.value),
                            contentDescription = "Driver image",contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp)
                        )*/
//                        Image(
//                  painter = painterResource(id = R.drawable.img_5),
//                  contentDescription = "My Image",
//                  contentScale = ContentScale.Crop
//              )
                    }
                      }


                /*   CoroutineScope(Dispatchers.IO).launch {
                       driverLoginViewModel?.getRouteList("")
                   }*/
//            }
            LaunchedEffect(lifecycleOwner) {
                driverLoginViewModel?.getVehicleList("")
                Log.e("DriverSelectRouteScreen", driverLoginViewModel?.vehicleList.toString())
                if (driverLoginViewModel?.getVehicleList("") != null) {
                    vehicleList =driverLoginViewModel.getVehicleList("")
                }
            }

        }

    }
}



/*LazyColumn(state = listState) {
    items<VehicleRouteItem>(items = messages) { message ->
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
                        driverLoginViewModel?.checkLocationSetting(
                            context = context,
                            onDisabled = { intentSenderRequest ->
                                settingResultRequest.launch(
                                    intentSenderRequest
                                )
                            },
                            onEnabled = {
                                if (permissions.all {
                                        ContextCompat.checkSelfPermission(
                                            context, it
                                        ) == PackageManager.PERMISSION_GRANTED
                                    }) {
                                    driverLoginViewModel.startLocationUpdates()
                                } else {
                                    launcherMultiplePermissions.launch(
                                        permissions
                                    )
                                }
                            })

                    },
                ),
        )
    }
}*/



