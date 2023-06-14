package com.tws.composebusalert.screens


import android.Manifest
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.tws.composebusalert.*
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.LocationDetails
import com.tws.composebusalert.responses.PassengerDetailResponse
import com.tws.composebusalert.responses.StoppingListDS
import com.tws.composebusalert.responses.Stoppings
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData


private const val LAT = -34
private const val LNG = 151.0
private const val MAP_ZOOM_LEVEL = 14f
private const val TWO_THOUSAND = 2000
private const val BOUND_PADDING = 50
private const val POLY_LINE_WIDTH = 15f
private const val NAVIGATE_LEVEL_ZOOM = 20f
private const val ANCHORING_VALUE: Float = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreenPassenger(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    context: Context
) {

    val coroutineScope = rememberCoroutineScope()
    val glaces: GooglePlacesInfoViewModel = hiltViewModel()
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )
    ComposeBusAlertTheme {
        var isMapLoaded by remember { mutableStateOf(false) }
        Box(
            Modifier
                .fillMaxSize()

                .background(Color.White)
        ) {
            GoogleMapViewPassenger(
                modifier = Modifier.fillMaxSize(),
                onMapLoaded = {
                    isMapLoaded = true
                },
                googlePlacesInfoViewModel = glaces,
                driverLoginViewModel = driverLoginViewModel,
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
}

private fun placeMarkersOnMap(it: List<Stoppings>?, mMap: GoogleMap?) {
    it?.let { stoppingList ->
        stoppingList.forEach {
            mMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .anchor(ANCHORING_VALUE, ANCHORING_VALUE)
                    .title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
            )
        }
    }
}

/* fun setMarkerMovement(latLng: LatLng) {
    if (marker == null) {
        marker = mMap?.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_medium))
        )
    }

    if (previousLatLng != latLng) {
        marker?.let { animateMarkerToGB(it, latLng, LatLngInterpolator.Spherical()) }
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, NAVIGATE_LEVEL_ZOOM))
        previousLatLng?.let { getBearing(latLng, it) }?.let { marker?.setRotation(it) }
        previousLatLng = latLng
    }
}*/


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GoogleMapViewPassenger(
    modifier: Modifier,
    onMapLoaded: () -> Unit,
    googlePlacesInfoViewModel: GooglePlacesInfoViewModel,
    driverLoginViewModel: DriverLoginViewModel,
) {
    val context1 = LocalContext.current
    val stops = MutableLiveData<LatLng>()
//    LaunchedEffect(Unit) {
//        driverLoginViewModel.getPassengersDetail(context1)
//    }
    var stop1 = LatLng(11.93702, 79.80877)
    val _makerList: MutableList<LatLng> = mutableListOf<LatLng>()
    val _makerList1: MutableList<LatLng> = mutableListOf<LatLng>()

    val passengerDetails = driverLoginViewModel.res.observeAsState(initial = emptyList())
    val pd1 by remember {
        mutableStateOf(passengerDetails.value)
    }

    Log.e("PD", "fulll data ${pd1}")
    var stopp1 by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    if (pd1 == null || pd1.isEmpty()) {
        Log.e("PD", pd1.toString())
        Log.e("PD", "Sorry Pd is null or MT")
        Log.e("PD", "null data ${pd1}")
//        stopp1 = LatLng(11.93702, 79.80877)
//        stopp2 = LatLng(11.92442, 79.80949)
//        stopp3 = LatLng(11.91877, 79.81569)

    } else {
        val items = pd1.size
        Log.e("PD", "!null data $pd1")
        _makerList1.add(
            LatLng(
                pd1[0].dropStopping?.address?.latitude!!,
                pd1[0].dropStopping?.address?.longitude!!
            )
        )
        Log.e("PD", "just _makerList1 data ${_makerList1.toString()}")
        stop1 = (LatLng(
            pd1[0].dropStopping?.address?.latitude!!,
            pd1[0].dropStopping?.address?.longitude!!
        ))
//        stops.value=
//            LatLng(pd1[0].dropStopping?.address?.latitude!!,
//                pd1[0].dropStopping?.address?.longitude!!)
//
//        _makerList1.add(stops.value!!)
//        stopp1 = _makerList1[0]
//        stopp1 = stops.value!!
        /* for (i in 0 until items) {
             if (pd1[i].dropStopping?.address?.latitude != null) {
                 Log.e("PD", pd1[i].dropStopping?.address?.latitude.toString())

                 _makerList1.add(LatLng(pd1[i].dropStopping?.address?.latitude!!,
                     pd1[i].dropStopping?.address?.longitude!!))
                 Log.e("PD", "just _makerList1 data ${_makerList1.toString() }")

             }
         }*/
    }
    val context = LocalContext.current
    val myLoation = LatLng(11.93082851941884, 79.76937826381055)


    val stop2 = LatLng(11.930390, 79.807510)

    val dataStore = StoreData(context)
//    val storedStoppings = dataStore.getStoppingList.collectAsState(initial = "")
//    val h = storedStoppings.value

//    if (stop1 != null) {
//        _makerList.add(stop1)
//    }

    _makerList1.add(stopp1)
//    _makerList1.add(stopp2)
//    _makerList1.add(stopp3)

//    if (stop2 != null) {
//        _makerList.add(stop2)
//    }
//    val pos2 by remember {
//        mutableStateOf(_makerList)
//    }
    val pos1 by remember {
        mutableStateOf(_makerList1)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLoation, 56f)
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(compassEnabled = true, zoomControlsEnabled = true)
        )
    }

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

            val markerClick: (Marker) -> Boolean = {
                Log.d("TAG", "${it.title} was clicked")
                false
            }
//            if (stop1 != null) {
//                if (stop2 != null) {
//                    googlePlacesInfoViewModel.getDirection(
//                        origin = "${stop1.latitude}, ${stop1.longitude}",
//                        destination = "${stop2.latitude}, ${stop2.longitude}",
//                        key = MapKey.KEY
//                    )
//                }
//            }

            val ll: MutableList<LatLng> = mutableListOf<LatLng>()
            ll.add(LatLng(11.93082851941884, 79.76937826381055))
            ll.add(LatLng(11.930833508790617, 79.79173868255614))
            ll.forEach { posistion ->
                Marker(
                    state = MarkerState(position = posistion),
                    title = "Stopping ",
                    snippet = "Marker in Stoppings ${posistion.latitude}, ${posistion.longitude}",
                    onClick = markerClick,
                    icon = driverLoginViewModel.bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.baseline_location_on_24
                    )
                )
                Log.e(
                    "New LAT",
                    "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
                )
            }

            if (pd1.isNotEmpty()) {
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            pd1[0].dropStopping?.address?.latitude!!,
                            pd1[0].dropStopping?.address?.longitude!!
                        )
                    ),
                    title = "Stopping ",
                    snippet = "Marker in Stoppings ${stop1.latitude}, ${stop1.longitude}",
                    onClick = markerClick,
                    icon = driverLoginViewModel?.bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.bus_stop
                    )
                )

            }

            pos1.forEach { posistion ->
                Marker(
                    state = MarkerState(position = posistion),
                    title = "Stopping ",
                    snippet = "Marker in Stoppings ${posistion.latitude}, ${posistion.longitude}",
                    onClick = markerClick,
                    icon = driverLoginViewModel.bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.bus_stop
                    )
                )
                Log.e(
                    "New LAT",
                    "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
                )
            }
            Polyline(
                points = ll,
                color = Color.Blue,
                width = 20f,
                onClick = {}
            )
        }
    }
}


@Composable
fun alert(showDialog: Boolean): Boolean {
    var a = showDialog
    if (a) {
        AlertDialog(shape = RoundedCornerShape(8.dp),
            containerColor = Color.White,
            modifier = Modifier.padding(5.dp),
            onDismissRequest = { a = false },
            title = { Text(text = "ALERT") },
            text = { Text(text = "Driver reached last stopping, Please close the Navigation") },
            confirmButton = {
                Text(
                    text = "NO",
                    modifier = Modifier.selectable(
                        selected = true,
                        onClick = {
                            a = !showDialog
                        },
                    ),
                )
            }
        )
    }
    return a
}

/*private fun drawOnMap() {
    startLocationServiceResponse.mapDetail?.mapData?.routes?.get(0)
        ?.overviewPolyline?.points?.let {
            drawPolyLineOnMap(PolyUtil.decode(it))
            placeMarkersOnMap(startLocationServiceResponse.mapDetail?.stoppings)
        }
}*/
fun drawPolyLineOnMap(list: List<LatLng?>, context: Context, mMap: GoogleMap) {
    val polyOptions = PolylineOptions()
    polyOptions.color(ContextCompat.getColor(context, Color.Gray.toArgb()))
    polyOptions.width(15f)
    polyOptions.addAll(list)
    mMap.clear()
    mMap.addPolyline(polyOptions)

    list[0]?.let {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            ).title("Start Location")
        )
    }

    list[list.size - 1]?.let {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            ).title("End Location")
        )
    }

    val builder = LatLngBounds.Builder()
    for (latLng in list) {
        if (latLng != null) {
            builder.include(latLng)
        }
    }
    val bounds = builder.build()
    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 50)
    mMap?.animateCamera(cu)
}



/*
package com.tws.composebusalert.screens


import android.Manifest
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.tws.composebusalert.*
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.maps.GooglePlacesInfoViewModel
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.LocationDetails
import com.tws.composebusalert.responses.PassengerDetailResponse
import com.tws.composebusalert.responses.StoppingListDS
import com.tws.composebusalert.responses.Stoppings
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData


private const val LAT = -34
private const val LNG = 151.0
private const val MAP_ZOOM_LEVEL = 14f
private const val TWO_THOUSAND = 2000
private const val BOUND_PADDING = 50
private const val POLY_LINE_WIDTH = 15f
private const val NAVIGATE_LEVEL_ZOOM = 20f
private const val ANCHORING_VALUE: Float = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreenPassenger(
    navController: NavController? = null,
    driverLoginViewModel: DriverLoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
//    loginViewModel: DriverLoginViewModel? = null,
    context: Context
) {
    /* val myLoation = LatLng(11.930390, 79.807510)
     GoogleMap(
         modifier = Modifier.fillMaxSize(),
         uiSettings=MapUiSettings(zoomControlsEnabled = true),
         cameraPositionState =CameraPositionState (CameraPosition(myLoation, 16f,0f,0f)),
         properties = MapProperties(mapType = MapType.HYBRID),
     )*/

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
//        a = LatLng(11.930390, 79.807510)
        Log.e("Lat", a.longitude.toString())



        Box(
            Modifier
                .fillMaxSize()

                .background(Color.White)
        ) {
            GoogleMapViewPassenger(
                modifier = Modifier.fillMaxSize(),
                onMapLoaded = {
                    isMapLoaded = true
                },
                googlePlacesInfoViewModel = glaces,
                11.93702,
                79.80877,
                driverLoginViewModel = driverLoginViewModel,
//                LatLng(11.930390, 79.807510)
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


          /*  if (currentLocation.latitude > 0.0) {
//                    Toast.makeText(context, "MAPPPPP", Toast.LENGTH_SHORT).show()
                GoogleMapViewPassenger(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = {
                        isMapLoaded = true
                    },
                    googlePlacesInfoViewModel = glaces,
                    11.93702,
                    79.80877,
                    driverLoginViewModel = driverLoginViewModel,
//                LatLng(11.930390, 79.807510)
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

            }*/
        }

    }
}

private fun placeMarkersOnMap(it: List<Stoppings>?,mMap:GoogleMap?) {
    it?.let { stoppingList ->
        stoppingList.forEach {
            mMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .anchor(ANCHORING_VALUE, ANCHORING_VALUE)
                    .title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
            )
        }
    }
}

/* fun setMarkerMovement(latLng: LatLng) {
    if (marker == null) {
        marker = mMap?.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_medium))
        )
    }

    if (previousLatLng != latLng) {
        marker?.let { animateMarkerToGB(it, latLng, LatLngInterpolator.Spherical()) }
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, NAVIGATE_LEVEL_ZOOM))
        previousLatLng?.let { getBearing(latLng, it) }?.let { marker?.setRotation(it) }
        previousLatLng = latLng
    }
}*/



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
    driverLoginViewModel: DriverLoginViewModel,
    a: LatLng
) {
    val context1 = LocalContext.current
    val stops = MutableLiveData<LatLng>()
    LaunchedEffect(Unit) {
        driverLoginViewModel.getPassengersDetail(context1)

    }
    val _makerList: MutableList<LatLng> = mutableListOf<LatLng>()
    val _makerList1: MutableList<LatLng> = mutableListOf<LatLng>()

    val passengerDetails = driverLoginViewModel.res.observeAsState(initial = emptyList())
    val pd1= passengerDetails.value


    Log.e("PD","fulll data ${pd1}" )
    var stopp1 =LatLng(11.930917523934855, 79.78670899810801)
    var stopp2 = LatLng(11.92442, 79.80949)
    var stopp3 = LatLng(11.91877, 79.81569)

    if (pd1 == null || pd1.isEmpty()) {
        Log.e("PD", pd1.toString())
        Log.e("PD","null data ${pd1}" )
//        stopp1 = LatLng(11.93702, 79.80877)
//        stopp2 = LatLng(11.92442, 79.80949)
//        stopp3 = LatLng(11.91877, 79.81569)

    } else {
        val items = pd1.size
        Log.e("PD","!null data ${pd1}" )
        _makerList1.add(LatLng(pd1[0].dropStopping?.address?.latitude!!,
            pd1[0].dropStopping?.address?.longitude!!))
//        _makerList1.add()
        Log.e("PD", "just _makerList1 data ${_makerList1.toString() }")
        stops.value=
            LatLng(pd1[0].dropStopping?.address?.latitude!!,
                pd1[0].dropStopping?.address?.longitude!!)

        _makerList1.add(stops.value!!)
        stopp1 = _makerList1[0]
       /* for (i in 0 until items) {
            if (pd1[i].dropStopping?.address?.latitude != null) {
                Log.e("PD", pd1[i].dropStopping?.address?.latitude.toString())

                _makerList1.add(LatLng(pd1[i].dropStopping?.address?.latitude!!,
                    pd1[i].dropStopping?.address?.longitude!!))
                Log.e("PD", "just _makerList1 data ${_makerList1.toString() }")

            }
        }*/
    }
    val asa = listOf(
        StoppingListDS(11.93702, 79.80877),
        StoppingListDS(11.92442, 79.80949),
        StoppingListDS(11.91877, 79.81569)
    )


    val context = LocalContext.current
    val myLoation = LatLng(11.93082851941884,79.76937826381055)
//    val myLoation = LatLng(a.latitude, a.longitude)


    val scope = rememberCoroutineScope()
//    val stop1 = driverLoginViewModel.stop1
//    val stop2 = driverLoginViewModel.stop2
    val stop1 = LatLng(11.93702, 79.80877)
    val stop2 = LatLng(11.930390, 79.807510)

    val dataStore = StoreData(context)
    val storedStoppings = dataStore.getStoppingList.collectAsState(initial = "")
    val h = storedStoppings.value
    Log.d("LATLONG", "${myLoation.latitude}${myLoation.longitude} was clicked")

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
//    _makerList1.add(stopp2)
//    _makerList1.add(stopp3)

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
            MapUiSettings(compassEnabled = true, zoomControlsEnabled = true)
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
           /* LaunchedEffect(locationState.value) {
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
            }*/

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

//            Marker(
//                state = MarkerState(position = myLoation),
//                title = "My Location ",
//                snippet = "Marker in Stoppings ${myLoation.latitude}, ${myLoation.longitude}",
//                onClick = markerClick,
//                icon = driverLoginViewModel.bitmapDescriptorFromVector(
//                    LocalContext.current, R.drawable.baseline_location_on_24
//                )
//            )
//            Log.e(
//                "LATLLLLLL", "${myLoation.latitude.toString()}    ${myLoation.longitude.toString()}"
//            )
            val ll: MutableList<LatLng> = mutableListOf<LatLng>()

            ll.add(LatLng(11.93082851941884,79.76937826381055))

            ll.add(LatLng( 11.930833508790617,79.79173868255614))
            ll.forEach { posistion ->
                Marker(
                    state = MarkerState(position = posistion),
                    title = "Stopping ",
                    snippet = "Marker in Stoppings ${posistion.latitude}, ${posistion.longitude}",
                    onClick = markerClick,
                    icon = driverLoginViewModel.bitmapDescriptorFromVector(
                        LocalContext.current, R.drawable.baseline_location_on_24
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
//                    icon = driverLoginViewModel?.bitmapDescriptorFromVector(
//                        LocalContext.current, R.drawable.bus_stop
//                    )
                )

                Log.e(
                    "New LAT",
                    "${posistion.latitude.toString()}    ${posistion.longitude.toString()}"
                )
            }



            Polyline(
                points = ll,
                color = Color.Blue,
                width = 20f,
                onClick = {}
            )
//            Polyline(points = googlePlacesInfoViewModel.polyLinesPoints.value,
//                color = Color.Blue,
//                width = 20f,
//                onClick = {})

        }

    }
}


@Composable
fun alert(showDialog:Boolean):Boolean {
    var a=showDialog
    if (a) {
        AlertDialog(shape = RoundedCornerShape(8.dp),
            containerColor = Color.White,
            modifier = Modifier.padding(5.dp),
            onDismissRequest = { a = false },
            title = { Text(text = "ALERT") },
            text = { Text(text = "Driver reached last stopping, Please close the Navigation") },
            confirmButton = {
                Text(
                    text = "NO",
                    modifier = Modifier.selectable(
                        selected = true,
                        onClick = {
                            a=!showDialog
                        },
                    ),
                )
            }
        )
    }
    return a
}

/*private fun drawOnMap() {
    startLocationServiceResponse.mapDetail?.mapData?.routes?.get(0)
        ?.overviewPolyline?.points?.let {
            drawPolyLineOnMap(PolyUtil.decode(it))
            placeMarkersOnMap(startLocationServiceResponse.mapDetail?.stoppings)
        }
}*/
 fun drawPolyLineOnMap(list: List<LatLng?>,context: Context,mMap:GoogleMap) {
    val polyOptions = PolylineOptions()
    polyOptions.color(ContextCompat.getColor(context, Color.Gray.toArgb()))
    polyOptions.width(15f)
    polyOptions.addAll(list)
    mMap.clear()
    mMap.addPolyline(polyOptions)

    list[0]?.let {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            ).title("Start Location")
        )
    }

    list[list.size - 1]?.let {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            ).title("End Location")
        )
    }

    val builder = LatLngBounds.Builder()
    for (latLng in list) {
        if (latLng != null) {
            builder.include(latLng)
        }
    }
    val bounds = builder.build()
    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 50)
    mMap?.animateCamera(cu)
}


/*private fun onMapLoaded() {
    // Perform actions after the map has finished loading

    // Call your custom function to draw on the map
    drawOnMap()

    // Start the location listener using your ViewModel
    viewModel.startLocationListener()
}


@Composable
fun MapView(
    modifier: Modifier = Modifier,
    onMapLoaded: () -> Unit
) {
    val context = LocalContext.current

    val map = rememberGoogleMapState()
    val cameraPositionState = rememberGoogleMapCameraPositionState()

    AndroidView({ context ->
        val mapView = MapView(context).apply {
            onCreate(null)
            getMapAsync { googleMap ->
                map.value = googleMap
                onMapLoaded()
            }
        }
        mapView
    }, modifier = modifier)

    LaunchedEffect(map.value) {
        map.value?.let { googleMap ->
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(LAT.toDouble(), LNG.toDouble())
                )
            )
            googleMap.animateCamera(
                CameraUpdateFactory.zoomTo(MAP_ZOOM_LEVEL),
                TWO_THOUSAND,
                null
            )

            googleMap.setOnMapLoadedCallback {
//                drawOnMap()
//                viewModel.startLocationListener()
            }
        }
    }
}*/
*/