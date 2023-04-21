package com.tws.composebusalert

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import com.tws.composebusalert.screens.DriverSelectRouteScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()
    lateinit var storeData: StoreData
    var phNo = ""
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")
    var check = ""
    //lateinit var
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        driverLoginViewModel.context=this
        storeData = StoreData(this)


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
                        MyScreen(driverLoginViewModel, this)
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

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
        Log.e("ResumeMain", driverLoginViewModel.listResponse.toString())

        Handler().postDelayed({
            Log.e("ResumeHandleMain", driverLoginViewModel.listResponse.toString())

            driverLoginViewModel.a.value = true
        }, 30000)
    }
}

@Composable
fun MyScreen(loginViewModel: DriverLoginViewModel, lifecycleOwner: LifecycleOwner) {
    var showDialog by remember { mutableStateOf(false) }
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
    }
    Navigation(
        "driver",
        driverLoginViewModel = loginViewModel,
        startDestination = Routes.Dashboard.name,
        lifecycleOwner = lifecycleOwner
    )
}

