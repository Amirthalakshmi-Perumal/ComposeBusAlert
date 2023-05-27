package com.tws.composebusalert.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.LocationDetails
import com.tws.composebusalert.responses.StoppingListDS
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import io.reactivex.internal.observers.ResumeSingleObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreenPassenger(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    context: Context
) {

    val dataStore = StoreData(context)
    var isInPictureInPictureMode = remember {
        mutableStateOf(false)
    }
    val storedDriverName = dataStore.getDriverName.collectAsState(initial = "")
    val storedRouteName = dataStore.getRouteName.collectAsState(initial = "")
    val pipScreen by driverLoginViewModel.w.collectAsState(initial = false)

    val rational = Rational(1, 2)
    val pip = remember { mutableStateOf(false) }

    val params = PictureInPictureParams.Builder()
        .setAspectRatio(rational)
        .build()
    val context = LocalContext.current
    val activity = (LocalContext.current as Activity)
    BackHandler(true) {
        pip.value = true
        activity.enterPictureInPictureMode(params)

//        activity.finish()
    }

    lateinit var a: LatLng


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

        a = LatLng(currentLocation.latitude, currentLocation.longitude)
        Log.e("Lat", a.longitude.toString())
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    pip.value = false
                }
                .background(Color.White)
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

            driverLoginViewModel.locationCallback = object : LocationCallback() {
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
                    driverLoginViewModel.locationRequired = true
                    driverLoginViewModel.startLocationUpdates()
                    Toast.makeText(
                        context, "Permission Granted", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

            if (currentLocation.latitude > 0.0) {
//                    Toast.makeText(context, "MAPPPPP", Toast.LENGTH_SHORT).show()
                GoogleMapViewPassenger(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = {
                        isMapLoaded = true
                    },
                    googlePlacesInfoViewModel = glaces,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    driverLoginViewModel = driverLoginViewModel,
                    a
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
//                if(!pip.value){
            if (pipScreen) {
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

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleMapViewPassenger(
    modifier: Modifier,
    onMapLoaded: () -> Unit,
    googlePlacesInfoViewModel: GooglePlacesInfoViewModel,
    latitude: Double,
    longitude: Double,
    driverLoginViewModel: DriverLoginViewModel?,
    a: LatLng
) {

    val asa = listOf(
        StoppingListDS(11.93702, 79.80877),
        StoppingListDS(11.92442, 79.80949),
        StoppingListDS(11.91877, 79.81569)
    )

    val context = LocalContext.current
    val myLoation = LatLng(11.930390, 79.807510)
//    val myLoation = LatLng(a.latitude, a.longitude)
//    val stop1 = LatLng(11.930390, 79.807510)
//    val stop2 = LatLng(11.940837, 79.763548)
    val stopp1 = LatLng(11.93702, 79.80877)
    val stopp2 = LatLng(11.92442, 79.80949)
    val stopp3 = LatLng(11.91877, 79.81569)

    val scope = rememberCoroutineScope()
    val stop1 = driverLoginViewModel?.stop1
    val stop2 = driverLoginViewModel?.stop2
    val dataStore = StoreData(context)
    val storedStoppings = dataStore.getStoppingList.collectAsState(initial = "")
    val h = storedStoppings.value
    Log.d("LATLONG", "${myLoation.latitude}${myLoation.longitude} was clicked")
    val _makerList: MutableList<LatLng> = mutableListOf<LatLng>()
    val _makerList1: MutableList<LatLng> = mutableListOf<LatLng>()

    /*LaunchedEffect(Unit) {
        val stoppingList = dataStore.getStoppingList.collect {
        }
        for (stopping in stoppingList ) {

        }
    }*/

    if (stop1 != null) {
        _makerList.add(stop1)
    }

    _makerList1.add(stopp1)
    _makerList1.add(stopp2)
    _makerList1.add(stopp3)

    if (stop2 != null) {
        _makerList.add(stop2)
    }
    val pos2 by remember {
        mutableStateOf(_makerList)
    }
    val pos1 by remember {
        mutableStateOf(_makerList1)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLoation, 56f)
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(compassEnabled = true, zoomControlsEnabled = false)
        )
    }
    val locationSource = MyLocationSource()
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
    Box {
        GoogleMap(
            modifier = modifier,
//            uiSettings=MapUiSettings(),
//            modifier = Modifier.size(25.dp),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = onMapLoaded,
            googleMapOptionsFactory = {
                GoogleMapOptions().camera(
                    CameraPosition.fromLatLngZoom(
                        myLoation, 50f
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
                    "New LAT",
                    "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
                )
            }
            pos1.forEach { posistion ->
                Marker(
                    state = MarkerState(position = posistion),
                    title = "Stopping ",
                    snippet = "Marker in Stoppings ${posistion.latitude}, ${posistion.longitude}",
                    onClick = markerClick,
                    icon = driverLoginViewModel?.bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.bus_stop
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
            Polyline(points = googlePlacesInfoViewModel.polyLinesPoints.value,
                color = Color.Blue,
                width = 20f,
                onClick = {})

        }

    }
}


