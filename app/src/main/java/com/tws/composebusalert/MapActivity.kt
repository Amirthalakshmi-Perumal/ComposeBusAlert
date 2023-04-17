package com.tws.composebusalert

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.compose.material.Button
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.*
import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
import com.tws.composebusalert.nav.Navigation
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.network.ResponseHandler
import com.tws.composebusalert.responses.LocationDetails
import com.tws.composebusalert.screens.Map
import com.tws.composebusalert.screens.RippleLoadingAnimation
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import com.tws.composebusalert.webservice.BusDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "MainActivityMap"

@AndroidEntryPoint
class MapActivity  : ComponentActivity(), OnMapReadyCallback {
    private val  driverLoginViewModel by viewModels<DriverLoginViewModel>()
    private var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequired = false
    private var counter = 0
    private lateinit var a: LatLng

    private val locationFlow = callbackFlow {

        while (true) {
            ++counter

            val location = newLocation(a)
            Log.d(TAG, "Location $counter: $location")
            trySend(location)

            delay(2_000)
        }


    }.shareIn(
        lifecycleScope, replay = 0, started = SharingStarted.WhileSubscribed()
    )

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isMapLoaded by remember { mutableStateOf(false) }
            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
            )
            val showDialog = remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            ComposeBusAlertTheme {
                val glaces: GooglePlacesInfoViewModel = hiltViewModel()
                val permissions = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION )
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
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
                                                "name",
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
//                        Map()
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
//                            navController = navController,
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
                            var buttonClicked by remember { mutableStateOf(false) }
//                        val onClick= Navigation(flavor = "driver", startDestination =Routes.DriverDashboard.name )
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
//                                                    buttonClicked = true
                                                }),
                                        )
                                        if (buttonClicked) {
                                            Navigation(
                                                flavor = "driver",
                                                startDestination = Routes.DriverDashboard.name,
                                                driverLoginViewModel = driverLoginViewModel
                                            )
                                        }
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


                            val context = LocalContext.current
                            val settingResultRequest = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult()
                            ) { activityResult ->
                                if (activityResult.resultCode == RESULT_OK) Log.d(
                                    "appDebug", "Accepted"
                                )
                                else {
                                    Log.d("appDebug", "Denied")
                                }
                            }

                            var currentLocation by remember {
                                mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
                            }

                            fusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(this@MapActivity)
                            locationCallback = object : LocationCallback() {
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
                                val areGranted =
                                    permissionsMap.values.reduce { acc, next -> acc && next }
                                if (areGranted) {
                                    locationRequired = true
                                    startLocationUpdates()
                                    Toast.makeText(
                                        context, "Permission Granted", Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
//                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Button(onClick = {
                                    checkLocationSetting(context = context,
                                        onDisabled = { intentSenderRequest ->
                                            settingResultRequest.launch(intentSenderRequest)
                                        },
                                        onEnabled = {
                                            if (permissions.all {
                                                    ContextCompat.checkSelfPermission(
                                                        context, it
                                                    ) == PackageManager.PERMISSION_GRANTED
                                                }) {
                                                startLocationUpdates()
                                            } else {
                                                launcherMultiplePermissions.launch(permissions)
                                            }
                                        })
                                }) {
                                    Text(text = "Get Location")
                                }
//                                Text(text = "Latitude : " + currentLocation.latitude)
//                                Text(text = "Longitude : " + currentLocation.longitude)
                                a = LatLng(currentLocation.latitude, currentLocation.longitude)

                                if (currentLocation.latitude > 0.0) {
                                    GoogleMapView(
                                        modifier = Modifier.fillMaxSize(),
                                        onMapLoaded = {
                                            isMapLoaded = true
                                        },
                                        googlePlacesInfoViewModel = glaces,
                                        currentLocation.latitude,
                                        currentLocation.longitude,
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

                            }
//                        }
                        }
                    }
                }
            }
        }
    }

    private fun checkLocationSetting(
        context: Context, onDisabled: (IntentSenderRequest) -> Unit, onEnabled: () -> Unit
    ) {

//        val locationRequest = LocationRequest.create().apply {
//            interval = 1000
//            fastestInterval = 1000
//            priority = Priority.PRIORITY_HIGH_ACCURACY
//        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(true).setMinUpdateIntervalMillis(50)
            .setMaxUpdateDelayMillis(100).build()

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }
    }


    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun GoogleMapView(
        modifier: Modifier,
        onMapLoaded: () -> Unit,
        googlePlacesInfoViewModel: GooglePlacesInfoViewModel,
        latitude: Double,
        longitude: Double
    ) {

        val myLoation = LatLng(latitude, longitude)
        val stop1 = LatLng(11.930390, 79.807510)
        val stop2 = LatLng(11.940837, 79.763548)
        Log.d("LATLONG", "${myLoation.latitude}${myLoation.longitude} was clicked")
        val _makerList: MutableList<LatLng> = mutableListOf<LatLng>()

        _makerList.add(stop1)
        _makerList.add(stop2)

        val pos2 by remember {
            mutableStateOf(_makerList)
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(myLoation, 56f)
        }

        val mapProperties by remember {
            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
        }

        val locationState = locationFlow.collectAsState(initial = newLocation(myLoation))

        val uiSettings by remember {
            mutableStateOf(
                MapUiSettings(compassEnabled = true)
            )
        }
        val locationSource = MyLocationSource1()

        GoogleMap(
            modifier = modifier,
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
            LaunchedEffect(locationState.value) {
                Log.d(TAG, "Updating blue dot on map...")
                locationSource.onLocationChanged(locationState.value)

                Log.d(TAG, "Updating camera position...")
                val cameraPosition = CameraPosition.fromLatLngZoom(
                    LatLng(
                        locationState.value.latitude, locationState.value.longitude
                    ), 56f
                )
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(cameraPosition), 2000
                )
            }

            val markerClick: (Marker) -> Boolean = {
                Log.d(TAG, "${it.title} was clicked")
                false
            }
            googlePlacesInfoViewModel.getDirection(
                origin = "${stop1.latitude}, ${stop1.longitude}",
                destination = "${stop2.latitude}, ${stop2.longitude}",
                key = MapKey.KEY
            )

            Marker(
                state = MarkerState(position = myLoation),
                title = "My Location ",
                snippet = "Marker in Stoppings ${myLoation.latitude}, ${myLoation.longitude}",
                onClick = markerClick,
                icon = bitmapDescriptorFromVector(
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
                    icon = bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.bus_medium
                    )
                )

                Log.e(
                    "New LAT",
                    "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
                )
            }

            Polyline(points = googlePlacesInfoViewModel.polyLinesPoints.value,
                color = Color.Blue,
                width = 20f,
                onClick = {})

        }

    }


    private fun bitmapDescriptorFromVector(
        context: Context, vectorResId: Int
    ): BitmapDescriptor? {

        // retrieve the actual drawable
        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        drawable.setBounds(0, 0, 120, 120)
        val bm = Bitmap.createBitmap(
            120, 120, Bitmap.Config.ARGB_8888
        )

        // draw it onto the bitmap
        val canvas = android.graphics.Canvas(bm)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest, it, Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.e("Map", "------------>Maaaap")
    }

}


private fun newLocation(a: LatLng): Location {
    val location = Location("MyLocationProvider")
    location.apply {
        latitude = a.latitude
        longitude = a.longitude
    }
    return location
}

private class MyLocationSource1 : LocationSource {

    private var listener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        this.listener = listener
    }

    override fun deactivate() {
        listener = null
    }

    fun onLocationChanged(location: Location) {
        listener?.onLocationChanged(location)
    }
}