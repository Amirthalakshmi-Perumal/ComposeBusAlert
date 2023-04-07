package com.tws.composebusalert.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*
import com.tws.composebusalert.MapActivity
import com.tws.composebusalert.R
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(navController: NavController? = null, driverLoginViewModel: DriverLoginViewModel?) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val name=  driverLoginViewModel?.firstName?.value.toString()

//    val mapObj = MapActivity()
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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

                        Text(
                            text = " Driver Details",
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Left
                        )

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
                                    name ,
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
                                    "Villiyanur",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                }
            },
            sheetPeekHeight = 0.dp,
            contentColor = Color.Red,
        ) {
            Box(Modifier.fillMaxSize()) {
                Map()


                TopAppBar(
                    title = {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(105.dp, 0.dp, 0.dp, 0.dp)
                        ) {

                            Text(
                                text = "DASHBOARD",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    },
                    modifier = Modifier.height(50.dp),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                    )
                )
                IconButton(onClick = {
                    coroutineScope.launch {

                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                            bottomSheetScaffoldState.bottomSheetState.expand()

                        } else {
                            bottomSheetScaffoldState.bottomSheetState.collapse()
                        }
                    }
                }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_5),
                        contentDescription = "My Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 60.dp, end = 5.dp)
                    )
                }
                RippleLoadingAnimation(
//                    navController = navController,
                    circleColor = MaterialTheme.colorScheme.onSecondary,
                    text = "Stop",
                    box1 = 200.dp,
                    box2 = 55.dp,
                    box3 = 125.dp,
                    fontSize = 15.sp,
                    alignment = Alignment.BottomCenter,
                    onClick = {

                        showDialog.value = true

                    })
                if (showDialog.value) {
                    AlertDialog(shape = RoundedCornerShape(8.dp),
                        containerColor = Color.White,
                        modifier = Modifier.padding(5.dp),
                        onDismissRequest = { showDialog.value = false },
                        title = { Text(text = "ALERT") },
                        text = { Text(text = "Are you sure to Stop?") },
                        confirmButton = {

                            Text(
                                text = "STOP",
                                modifier = Modifier.selectable(
                                    selected = true,
                                    onClick = {
                                        navController?.navigate(Routes.DriverDashboard.name)
                                    },
                                ),
                            )

                        },
                        dismissButton = {
                            Text(
                                text = "NO",
                                modifier = Modifier.selectable(
                                    selected = true,
                                    onClick = {
                                        showDialog.value = false
                                    },
                                ),
                            )
                        })

                }
            }
        }
    }

}


@Composable
fun Map() {
    val context= LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    LaunchedEffect(true) {
        val intent = Intent(context, MapActivity::class.java)
        launcher.launch(intent)
    }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                minimumHeight = 400
                minimumWidth = 400
                // Configure the MapView as needed
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            // Use the MapView as needed

        }
    )
   /* val pondy = LatLng(11.9416, 79.8083)
    val bang = LatLng(12.9716, 77.5946)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pondy, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = pondy),
            title = "Pondicherry",
            snippet = "Marker in Pondicherry"
        )
        Marker(
            state = MarkerState(position = bang),
            title = "Bangalore",
            snippet = "Marker in Bangalore"
        )
        val polylineOptions = PolylineOptions().apply {
            add(pondy, bang)
            color(Color.Blue.toArgb())
            width(5f)
        }

        Polyline(
            jointType = JointType.ROUND,
            points = listOf(pondy, bang),
            geodesic = true,
            color = Color.Blue,

            )

    }
*/
}

