package com.tws.composebusalert

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.nav.Navigation
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.tws.composebusalert.screens.DriverSelectRouteScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMapReadyCallback {
    @RequiresApi(Build.VERSION_CODES.M)
    private var pressedTime: Long = 0
    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()
    lateinit var storeData: StoreData
    var phNo = ""
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")
    var check = ""
     var locationRequired = false
    var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    var counter = 0
 /*   lateinit var a: LatLng

    val locationFlow = callbackFlow {
        while (true) {
            ++counter
            val location = driverLoginViewModel?.newLocation(a)
            Log.d("TAG", "Location $counter: $location")
            trySend(location)
            delay(2_000)
        }
    }.shareIn(
        lifecycleScope, replay = 0, started = SharingStarted.WhileSubscribed()
    )*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
//     driverLoginViewModel.getVehicleList("")
   /*  window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
     actionBar?.hide()*/
//        driverLoginViewModel.context=this
        driverLoginViewModel.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MainActivity)
        storeData = StoreData(this)
         locationCallback=driverLoginViewModel.locationCallback
         fusedLocationClient=driverLoginViewModel.fusedLocationClient

        lifecycleScope.launch {
            val no = storeData.getNo.first()
            check = storeData.getScreen.first()
            Log.d("MainActivity", "Stored no is $no")
//            check =storeData.getNo.first()
//            Log.d("MainActivity", "Stored token is $token")
            Log.d("MainActivity", "Stored screen is $check")
        }
        runBlocking {
            check = storeData.getScreen.first()
        }
        setContent {

            ComposeBusAlertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    if (check == "") {
                        Log.e("Main phno", phNo)
                        MyScreen(driverLoginViewModel, this,this)
                    }
                    else {
                        Navigation(
                            flavor = "driver",
                            startDestination = Routes.DriverDashboard.name,
                            driverLoginViewModel = driverLoginViewModel,
                            lifecycleOwner = this, context = this,
//                            locationFlow=locationFlow
                        )
                    }
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
//        driverLoginViewModel.getVehicleList("")
        Log.e("ResumeMain", driverLoginViewModel.listResponse.toString())
        if (locationRequired) {
            driverLoginViewModel.startLocationUpdates()
        }
    }
   /* @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        // on below line we are checking if the press time is greater than 2 sec
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            // if time is greater than 2 sec we are closing the application.
            super.onBackPressed()
            finish()
        } else {
            // in else condition displaying a toast message.
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        // on below line initializing our press time variable
        pressedTime = System.currentTimeMillis();
    }*/
    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    private fun newLocation(a: LatLng): Location {
        val location = Location("MyLocationProvider")
        location.apply {
            latitude = a.latitude
            longitude = a.longitude
        }
        return location
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.e("Map", "------------>Maaaap")
    }

}
 class MyLocationSource : LocationSource {

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
@Composable
fun MyScreen(loginViewModel: DriverLoginViewModel, lifecycleOwner: LifecycleOwner,context: Context,
//             locationFlow: SharedFlow<Location?>
) {
    /*var showDialog by remember { mutableStateOf(false) }
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog = true
            }
        }
    }
    val onBackPress: () -> Unit = { onBackPressedCallback.handleOnBackPressed() }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher) {
        onBackPressedCallback.isEnabled = true
        backDispatcher?.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }*/
    Navigation(
        "driver",
        driverLoginViewModel = loginViewModel,
        startDestination = Routes.Dashboard.name,
        lifecycleOwner = lifecycleOwner,
        context = context,
//        locationFlow=locationFlow
    )
}

