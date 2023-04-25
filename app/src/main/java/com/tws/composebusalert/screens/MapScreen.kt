package com.tws.composebusalert.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
//import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.tws.composebusalert.*
import com.tws.composebusalert.R
import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.LocationDetails
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel?,
//    locationFlow: SharedFlow<Location?>,

) {
    val context = LocalContext.current

    val activity = (LocalContext.current as Activity)
    BackHandler(true) {
        activity.finish()
    }
    lateinit var a: LatLng

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val name = driverLoginViewModel?.firstName?.value.toString()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

//    val mapObj = MapActivity()
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val glaces: GooglePlacesInfoViewModel = hiltViewModel()
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )
    ComposeBusAlertTheme {

        var currentLocation by remember {
            mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
        }
        var isMapLoaded by remember { mutableStateOf(false) }
        /*      GoogleMapView(
                  modifier = Modifier.fillMaxSize(),
                  onMapLoaded = {
                      isMapLoaded = true
                  },
                  googlePlacesInfoViewModel = glaces,
                  currentLocation.latitude,
                  currentLocation.longitude,
                  driverLoginViewModel = driverLoginViewModel,
      //            locationFlow = locationFlow
              )*/
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
            a = LatLng(currentLocation.latitude, currentLocation.longitude)
            Log.e("Lat", a.longitude.toString())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {


           /*     GoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = {
                        isMapLoaded = true
                    },
                    googlePlacesInfoViewModel = glaces,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    driverLoginViewModel = driverLoginViewModel,
                    a
                )*/


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
                                modifier = Modifier.selectable(selected = true, onClick = {
                                    navController?.navigate(Routes.DriverDashboard.name)
                                }),
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
                driverLoginViewModel?.locationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        for (lo in p0.locations) {
                            Log.e("AAA", "Launched onLocationResult")
                            currentLocation = LocationDetails(lo.latitude, lo.longitude)
                        }
                    }
                }
                val launcherMultiplePermissions = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsMap ->
                    val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                    if (areGranted) {
                        driverLoginViewModel?.locationRequired = true
                        driverLoginViewModel?.startLocationUpdates()
                        Toast.makeText(
                            context, "Permission Granted", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

            /*    Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
              *//*      Button(
                        onClick = {
                            driverLoginViewModel?.checkLocationSetting(context = context,
                                onDisabled = { intentSenderRequest ->
                                    settingResultRequest.launch(intentSenderRequest)
                                },
                                onEnabled = {
                                    if (permissions.all {
                                            ContextCompat.checkSelfPermission(
                                                context, it
                                            ) == PackageManager.PERMISSION_GRANTED
                                        }) {
                                        driverLoginViewModel.startLocationUpdates()
                                    } else {
                                        launcherMultiplePermissions.launch(permissions)
                                    }
                                })
                        },
                    ) {
                        Text(text = "Get Location", color = Color.White)
                    }*//*
                }*/
                if (currentLocation.latitude > 0.0) {
                    Toast.makeText(context, "MAPPPPP", Toast.LENGTH_SHORT).show()
                    GoogleMapView(
                        modifier = Modifier.fillMaxSize(),
                        onMapLoaded = {
                            isMapLoaded = true
                        },
                        googlePlacesInfoViewModel = glaces,
                        currentLocation.latitude,
                        currentLocation.longitude,
                        driverLoginViewModel = driverLoginViewModel,
                        a
//                        locationFlow = locationFlow
                    )

                    if (!isMapLoaded) {
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxSize(),
                            visible = !isMapLoaded,
                            enter = EnterTransition.None,
                            exit = fadeOut()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .wrapContentSize()
                            )
                        }
                    }

                }
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
                            .padding(top = 54.dp, end = 25.dp, bottom = 4.dp)
                    )
                }
                RippleLoadingAnimation(circleColor = MaterialTheme.colorScheme.onSecondary,
                    text = "Stop",
                    box1 = 200.dp,
                    box2 = 55.dp,
                    box3 = 125.dp,
                    fontSize = 15.sp,
                    alignment = Alignment.BottomCenter,
                    onClick = {
                        showDialog.value = true
                    })


            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleMapView(
    modifier: Modifier,
    onMapLoaded: () -> Unit,
    googlePlacesInfoViewModel: GooglePlacesInfoViewModel,
    latitude: Double,
    longitude: Double,
    driverLoginViewModel: DriverLoginViewModel?,
    a: LatLng
//    locationFlow: SharedFlow<Location?>
) {

//    val myLoation = LatLng(11.930390, 79.807510)
    val myLoation = LatLng(a.latitude, a.longitude)
//    val stop1 = LatLng(11.930390, 79.807510)
//    val stop2 = LatLng(11.940837, 79.763548)
val stop1 = driverLoginViewModel?.stop1
    val stop2 = driverLoginViewModel?.stop2

    Log.d("LATLONG", "${myLoation.latitude}${myLoation.longitude} was clicked")
    val _makerList: MutableList<LatLng> = mutableListOf<LatLng>()

    if (stop1 != null) {
        _makerList.add(stop1)
    }
    if (stop2 != null) {
        _makerList.add(stop2)
    }
    val context = LocalContext.current
    val pos2 by remember {
        mutableStateOf(_makerList)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLoation, 56f)
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(compassEnabled = true)
        )
    }
    val locationSource = MyLocationSource()
    val lifecycleOwner = LocalLifecycleOwner.current
//    val lifecycleScope = LocalLifecycleScope.current
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    val locationFlow = remember {
        callbackFlow {
            while (true) {
                val location = driverLoginViewModel?.newLocation(a)
                trySend(location)
                delay(2_000)
            }
        }.shareIn(
            lifecycleScope, replay = 0, started = SharingStarted.WhileSubscribed()
        )
    }
    val locationState =
        locationFlow.collectAsState(initial = driverLoginViewModel?.newLocation(myLoation))
Box{
    GoogleMap(
        modifier = modifier,
//            modifier = Modifier.size(25.dp),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded,
        googleMapOptionsFactory = {
            GoogleMapOptions().camera(
                CameraPosition.fromLatLngZoom(
                    myLoation, 56f
                )
            )
        },
    ) {
        Log.d("TAG", " on map...")

        LaunchedEffect(locationState.value) {
            Log.d("TAG", "Updating blue dot on map...")
            locationState.value?.let { locationSource.onLocationChanged(it) }

            Log.d("TAG", "Updating camera position...")
            val cameraPosition = locationState.value?.let {
                LatLng(
                    it.latitude, locationState.value!!.longitude
                )
            }?.let {
                CameraPosition.fromLatLngZoom(
                    it, 56f
                )
            }
            cameraPosition?.let { CameraUpdateFactory.newCameraPosition(it) }?.let {
                cameraPositionState.animate(
                    it, 2000
                )
            }
        }

        val markerClick: (Marker) -> Boolean = {
            Log.d("TAG", "${it.title} was clicked")
            false
        }
        if (stop1 != null) {
            if (stop2 != null) {
                googlePlacesInfoViewModel.getDirection(
                    origin = "${stop1.latitude}, ${stop1.longitude}",
                    destination = "${stop2.latitude}, ${stop2.longitude}",
                    key = MapKey.KEY
                )
            }
        }

        Marker(
            state = MarkerState(position = myLoation),
            title = "My Location ",
            snippet = "Marker in Stoppings ${myLoation.latitude}, ${myLoation.longitude}",
            onClick = markerClick,
            icon = driverLoginViewModel?.bitmapDescriptorFromVector(
                LocalContext.current, R.drawable.baseline_location_on_24
            )
        )
        Log.e(
            "LATLLLLLL", "${myLoation.latitude.toString()}    ${myLoation.longitude.toString()}"
        )

        pos2.forEach { posistion ->
            Marker(
                state = MarkerState(position = posistion),
                title = "Stopping ",
                snippet = "Marker in Stoppings ${posistion.latitude}, ${posistion.longitude}",
                onClick = markerClick,
                icon = driverLoginViewModel?.bitmapDescriptorFromVector(
                    LocalContext.current, R.drawable.bus_medium
                )
            )

            Log.e(
                "New LAT", "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
            )
        }

        Polyline(points = googlePlacesInfoViewModel.polyLinesPoints.value,
            color = Color.Blue,
            width = 20f,
            onClick = {})

    }

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


}



}

