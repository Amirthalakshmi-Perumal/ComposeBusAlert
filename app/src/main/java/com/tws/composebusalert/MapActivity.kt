package com.tws.composebusalert
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.location.Location
//import android.location.LocationManager
//import android.os.Bundle
//import androidx.compose.material.Button
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.compose.setContent
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material3.*
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.lifecycleScope
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.*
//import com.google.android.gms.maps.model.*
//import com.google.android.gms.tasks.Task
//import com.google.maps.android.compose.*
//import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
//import com.tws.composebusalert.nav.Navigation
//import com.tws.composebusalert.nav.Routes
//import com.tws.composebusalert.network.ResponseHandler
//import com.tws.composebusalert.responses.LocationDetails
//import com.tws.composebusalert.screens.GoogleMapView
//import com.tws.composebusalert.screens.Map
//import com.tws.composebusalert.screens.RippleLoadingAnimation
//import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
//import com.tws.composebusalert.viewmodel.DriverLoginViewModel
//import com.tws.composebusalert.webservice.BusDataSource
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.flow.shareIn
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//const val TAG = "MainActivityMap"
//
//@AndroidEntryPoint
//class MapActivity : ComponentActivity(), OnMapReadyCallback {
//    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()
//    private var locationCallback: LocationCallback? = null
//    var fusedLocationClient: FusedLocationProviderClient? = null
//    private var locationRequired = false
//    private var counter = 0
//    private lateinit var a: LatLng
//    val lifeCycleOwner = this
//    private val locationFlow = callbackFlow {
//        while (true) {
//            ++counter
//            val location = newLocation(a)
//            Log.d(TAG, "Location $counter: $location")
//            trySend(location)
//            delay(2_000)
//        }
//    }.shareIn(
//        lifecycleScope, replay = 0, started = SharingStarted.WhileSubscribed()
//    )
//    val locationState = this.locationFlow.collectAsState(initial = driverLoginViewModel?.newLocation(myLoation))
//
//    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            var isMapLoaded by remember { mutableStateOf(false) }
//            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
//                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
//            )
//            val showDialog = remember { mutableStateOf(false) }
//            val coroutineScope = rememberCoroutineScope()
//            val context = this
//            var buttonClicked = remember { mutableStateOf(false) }
//
//            ComposeBusAlertTheme {
//                val glaces: GooglePlacesInfoViewModel = hiltViewModel()
//                val permissions = arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//                Surface(
//                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
//                ) {
//                    BottomSheetScaffold(
//                        scaffoldState = bottomSheetScaffoldState,
//                        sheetContent = {
//                            Box(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .height(120.dp)
//                                    .background(Color(0xFF03A9F4))
//                            ) {
//                                Column(
//                                    Modifier.fillMaxSize(),
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//
//                                    Text(
//                                        text = " Driver Details",
//                                        fontSize = 20.sp,
//                                        color = Color.White,
//                                        textAlign = TextAlign.Left
//                                    )
//
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .background(MaterialTheme.colorScheme.onTertiary)
//                                            .padding(8.dp),
//                                    ) {
//                                        Column(Modifier.padding(2.dp)) {
//                                            Text(
//                                                "Driver Name",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//
//                                                )
//                                            Text(
//                                                "No of Students",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//
//                                                )
//                                            Text(
//                                                "Bus Routes",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//
//                                                )
//                                        }
//                                        Spacer(modifier = Modifier.width(20.dp))
//                                        Column(Modifier.padding(2.dp)) {
//                                            Text(
//                                                "name",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//                                            )
//                                            Text(
//                                                "35",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//                                            )
//                                            Text(
//                                                "Villiyanur",
//                                                fontWeight = FontWeight.Normal,
//                                                fontSize = 15.sp,
//                                                color = Color.Black,
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        },
//                        sheetPeekHeight = 0.dp,
//                        contentColor = Color.Red,
//                    ) {
//                        Box(Modifier.fillMaxSize()) {
////                        Map()
//
////                        val onClick= Navigation(flavor = "driver", startDestination =Routes.DriverDashboard.name )
//                            if (showDialog.value) {
//                                AlertDialog(shape = RoundedCornerShape(8.dp),
//                                    containerColor = Color.White,
//                                    modifier = Modifier.padding(5.dp),
//                                    onDismissRequest = { showDialog.value = false },
//                                    title = { Text(text = "ALERT") },
//                                    text = { Text(text = "Are you sure to Stop?") },
//                                    confirmButton = {
//                                        Text(
//                                            text = "STOP",
//                                            modifier = Modifier.selectable(selected = true,
//                                                onClick = {
//                                                    buttonClicked.value = true
//                                                }),
//                                        )
//                                        if (buttonClicked.value) {
//                                            Log.e("MAp Act", "NAV")
//                                            Navigation(
//                                                "driver",
//                                                startDestination = Routes.DriverDashboard.name,
//                                                driverLoginViewModel = driverLoginViewModel,
//                                                lifecycleOwner = lifeCycleOwner,
//                                                context = context
//                                            )
//
////                                            buttonClicked.value = false
//
//                                        }
//                                    },
//                                    dismissButton = {
//                                        Text(
//                                            text = "NO",
//                                            modifier = Modifier.selectable(
//                                                selected = true,
//                                                onClick = {
//                                                    showDialog.value = false
//                                                },
//                                            ),
//                                        )
//                                    })
//
//                            }
//                            val context = LocalContext.current
//                            val settingResultRequest = rememberLauncherForActivityResult(
//                                contract = ActivityResultContracts.StartIntentSenderForResult()
//                            ) { activityResult ->
//                                if (activityResult.resultCode == RESULT_OK) Log.d(
//                                    "appDebug", "Accepted"
//                                )
//                                else {
//                                    Log.d("appDebug", "Denied")
//                                }
//                            }
//
//                            var currentLocation by remember {
//                                mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
//                            }
//
//                            fusedLocationClient =
//                                LocationServices.getFusedLocationProviderClient(this@MapActivity)
//                            locationCallback = object : LocationCallback() {
//                                override fun onLocationResult(p0: LocationResult) {
//                                    for (lo in p0.locations) {
//                                        Log.e("AAA", "Launched onLocationResult")
//                                        currentLocation = LocationDetails(lo.latitude, lo.longitude)
//                                    }
//                                }
//                            }
//
//                            val launcherMultiplePermissions = rememberLauncherForActivityResult(
//                                ActivityResultContracts.RequestMultiplePermissions()
//                            ) { permissionsMap ->
//                                val areGranted =
//                                    permissionsMap.values.reduce { acc, next -> acc && next }
//                                if (areGranted) {
//                                    locationRequired = true
//                                    startLocationUpdates()
//                                    Toast.makeText(
//                                        context, "Permission Granted", Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT)
//                                        .show()
//                                }
//                            }
//
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(Color.LightGray),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                TopAppBar(
//                                    title = {
//                                        Row(
//                                            Modifier
//                                                .fillMaxWidth()
//                                                .padding(105.dp, 0.dp, 0.dp, 0.dp)
//                                        ) {
//
//                                            Text(
//                                                text = "DASHBOARD",
//                                                textAlign = TextAlign.Center,
//                                                fontSize = 20.sp,
//                                                fontWeight = FontWeight.Bold,
//                                                modifier = Modifier
//                                                    .padding(10.dp)
//                                                    .align(Alignment.CenterVertically)
//                                            )
//                                        }
//                                    },
//                                    modifier = Modifier.height(50.dp),
//                                    colors = TopAppBarDefaults.smallTopAppBarColors(
//                                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                                        containerColor = MaterialTheme.colorScheme.onSecondary,
//                                    )
//                                )
//                                Button(onClick = {
//                                    checkLocationSetting(context = context,
//                                        onDisabled = { intentSenderRequest ->
//                                            settingResultRequest.launch(intentSenderRequest)
//                                        },
//                                        onEnabled = {
//                                            if (permissions.all {
//                                                    ContextCompat.checkSelfPermission(
//                                                        context, it
//                                                    ) == PackageManager.PERMISSION_GRANTED
//                                                }) {
//                                                startLocationUpdates()
//                                            } else {
//                                                launcherMultiplePermissions.launch(permissions)
//                                            }
//                                        })
//                                }) {
//                                    Text(text = "Get Location")
//                                }
//                                Box(Modifier.fillMaxWidth()) {
//                                    IconButton(onClick = {
//                                        coroutineScope.launch {
//
//                                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
//                                                bottomSheetScaffoldState.bottomSheetState.expand()
//
//                                            } else {
//                                                bottomSheetScaffoldState.bottomSheetState.collapse()
//                                            }
//                                        }
//                                    }, modifier = Modifier.align(Alignment.TopEnd)) {
//                                        Image(
//                                            painter = painterResource(id = R.drawable.img_5),
//                                            contentDescription = "My Image",
//                                            contentScale = ContentScale.Crop,
//                                            modifier = Modifier
//                                                .align(Alignment.TopEnd)
//                                                .padding(top = 2.dp, end = 25.dp, bottom = 4.dp)
//                                        )
//                                    }
//                                }
//                                if (currentLocation.latitude > 0.0) {
//                                    GoogleMapView(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(320.dp),
//                                        onMapLoaded = {
//                                            isMapLoaded = true
//                                        },
//                                        googlePlacesInfoViewModel = glaces,
//                                        currentLocation.latitude,
//                                        currentLocation.longitude,driverLoginViewModel
//                                    )
//
//                                    if (!isMapLoaded) {
//                                        AnimatedVisibility(
//                                            modifier = Modifier.fillMaxSize(),
//                                            visible = !isMapLoaded,
//                                            enter = EnterTransition.None,
//                                            exit = fadeOut()
//                                        ) {
//                                            CircularProgressIndicator(
//                                                modifier = Modifier
//                                                    .background(MaterialTheme.colorScheme.background)
//                                                    .wrapContentSize()
//                                            )
//                                        }
//                                    }
//                                }
//                                RippleLoadingAnimation(circleColor = MaterialTheme.colorScheme.onSecondary,
//                                    text = "Stop",
//                                    box1 = 200.dp,
//                                    box2 = 55.dp,
//                                    box3 = 125.dp,
//                                    fontSize = 15.sp,
//                                    alignment = Alignment.BottomCenter,
//                                    onClick = {
//                                        showDialog.value = true
//                                    })
//                            }
//                            a = LatLng(currentLocation.latitude, currentLocation.longitude)
//                            Log.e("Lat", a.longitude.toString())
//
//                            GoogleMapView(
//                                modifier = Modifier.fillMaxSize(),
//                                onMapLoaded = {
//                                    isMapLoaded = true
//                                },
//                                googlePlacesInfoViewModel = glaces,
//                                currentLocation.latitude,
//                                currentLocation.longitude,
//                                driverLoginViewModel
//                            )
//
////                        }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun checkLocationSetting(
//        context: Context, onDisabled: (IntentSenderRequest) -> Unit, onEnabled: () -> Unit
//    ) {
//
////        val locationRequest = LocationRequest.create().apply {
////            interval = 1000
////            fastestInterval = 1000
////            priority = Priority.PRIORITY_HIGH_ACCURACY
////        }
//        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
//            .setWaitForAccurateLocation(true).setMinUpdateIntervalMillis(50)
//            .setMaxUpdateDelayMillis(100).build()
//
//        val client: SettingsClient = LocationServices.getSettingsClient(context)
//        val builder: LocationSettingsRequest.Builder =
//            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//
//        val gpsSettingTask: Task<LocationSettingsResponse> =
//            client.checkLocationSettings(builder.build())
//
//        gpsSettingTask.addOnSuccessListener { onEnabled() }
//        gpsSettingTask.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    val intentSenderRequest =
//                        IntentSenderRequest.Builder(exception.resolution).build()
//                    onDisabled(intentSenderRequest)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // ignore here
//                }
//            }
//        }
//    }
//
//
//
//
//    private fun bitmapDescriptorFromVector(
//        context: Context, vectorResId: Int
//    ): BitmapDescriptor? {
//
//        // retrieve the actual drawable
//        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
//        drawable.setBounds(0, 0, 120, 120)
//        val bm = Bitmap.createBitmap(
//            120, 120, Bitmap.Config.ARGB_8888
//        )
//
//        // draw it onto the bitmap
//        val canvas = android.graphics.Canvas(bm)
//        drawable.draw(canvas)
//        return BitmapDescriptorFactory.fromBitmap(bm)
//    }
//
//
//    @SuppressLint("MissingPermission")
//    private fun startLocationUpdates() {
//        locationCallback?.let {
//            val locationRequest = LocationRequest.create().apply {
//                interval = 10000
//                fastestInterval = 5000
//                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            }
//            fusedLocationClient?.requestLocationUpdates(
//                locationRequest, it, Looper.getMainLooper()
//            )
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        driverLoginViewModel.getDriverDetailsVM()
//        if (locationRequired) {
//            startLocationUpdates()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
//    }
//
//    override fun onMapReady(p0: GoogleMap) {
//        Log.e("Map", "------------>Maaaap")
//    }
//
//}
//
//
//private fun newLocation(a: LatLng): Location {
//    val location = Location("MyLocationProvider")
//    location.apply {
//        latitude = a.latitude
//        longitude = a.longitude
//    }
//    return location
//}
//
//private class MyLocationSource1 : LocationSource {
//
//    private var listener: LocationSource.OnLocationChangedListener? = null
//
//    override fun activate(listener: LocationSource.OnLocationChangedListener) {
//        this.listener = listener
//    }
//
//    override fun deactivate() {
//        listener = null
//    }
//
//    fun onLocationChanged(location: Location) {
//        listener?.onLocationChanged(location)
//    }
//}
