package com.tws.composebusalert

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.tws.composebusalert.screens.NetworkProbDialog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMapReadyCallback {
    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()
    lateinit var storeData: StoreData
    var phNo = ""
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")
    var check = ""
     var locationRequired = false
    var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        driverLoginViewModel.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MainActivity)
        storeData = StoreData(this)
         locationCallback=driverLoginViewModel.locationCallback
         fusedLocationClient=driverLoginViewModel.fusedLocationClient

        lifecycleScope.launch {
            val no = storeData.getNo.first()
            check = storeData.getScreen.first()
            Log.d("MainActivity", "Stored no is $no")
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
                    var internet = isInternetAvailable(this)
                    val infoDialog = remember { mutableStateOf(false) }
                    if (!internet) {
                        infoDialog.value = true
                        if (infoDialog.value) {
                            NetworkProbDialog(
                                title = "Whoops!",
                                desc = "No Internet Connection found.\n" +
                                        "Check your connection or try again.",
                                onDismiss = {
                                    infoDialog.value = false
                                }
                            )
                        }
//                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }else{
                        if (check == "") {
                            Log.e("Main phno", phNo)
                            MyScreen(driverLoginViewModel, this,)
                        }
                        else {
                            Navigation(
                                flavor = "driver",
                                startDestination = Routes.DriverDashboard.name,
                                driverLoginViewModel = driverLoginViewModel,
                                lifecycleOwner = this
                            )
                        }
                    }

                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
        Log.e("ResumeMain", driverLoginViewModel.listResponse.toString())
        if (locationRequired) {
            driverLoginViewModel.startLocationUpdates()
        }
    }
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



fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
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
fun MyScreen(loginViewModel: DriverLoginViewModel, lifecycleOwner: LifecycleOwner
) {

    Navigation(
        "driver",
        driverLoginViewModel = loginViewModel,
        startDestination = Routes.Dashboard.name,
        lifecycleOwner = lifecycleOwner,
    )
}

