package com.tws.composebusalert

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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tws.composebusalert.data.UserStore
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.nav.Navigation
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()
    lateinit var storeData: StoreData
    var phNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storeData = StoreData(this)
        //        val storedRouteId = dataStore.getrouteId.collectAsState(initial = "")
//        val dataStore = StoreData(this)
//        val storedRouteId = dataStore.getrouteId
//        Log.e("RouteID", storedRouteId.toString())
        this.storeData.getNo.asLiveData().observe(this) {
            phNo = it
            Log.e("Main", it)
            if (phNo == "") {
                Log.e("Main phno", phNo)
                setContent {
                    ComposeBusAlertTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
//                            Mobile_Number()
                            MyScreen(driverLoginViewModel,this)
                        }
                    }
                }

            } else {
//                driverLoginViewModel.getRouteList("")
                var listResponse: List<RouteListResponse>? = null
                Log.e("MainRess", driverLoginViewModel.listResponse?.size.toString())
                setContent {
                    ComposeBusAlertTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
//                            driverLoginViewModel.getRouteList(null)
//                            listResponse = driverLoginViewModel.getRouteList("")
//                            Log.e("MainList", listResponse.toString())
//                            DriverSelectRouteScreen(loginViewModel=driverLoginViewModel)
                            Navigation(
                                flavor = "driver",
                                startDestination = Routes.DriverSelectRouteScreen.name,
                                driverLoginViewModel = driverLoginViewModel,
                                lifecycleOwner=this
                            )
                        }
                    }
                }
            }
        }


         /* setContent {
  //            val sharedPreferences: SharedPreferences =
  //                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
              val loginViewModel = viewModel(modelClass = DriverLoginViewModel::class.java)
              ComposeBusAlertTheme {
                  Surface(
                      modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                  ) {
                      val context = LocalContext.current
                      val store = UserStore(context)
                      val tokenText = store.getAccessToken.collectAsState(initial = "")

                      var isAuthSuccessful by remember { mutableStateOf(false) }
  //                    val sharedIdValue = sharedPreferences.getInt("id_key", 0)
  //                    val sharedNameValue = sharedPreferences.getString("name_key", "defaultname")


                    if (sharedIdValue == 0 && sharedNameValue.equals("defaultname")) {
                         MyScreen(loginViewModel)

                    } else {
                        Navigation(
                            flavor = "driver",
                            startDestination = Routes.DriverSelectRouteScreen.name,
                            driverLoginViewModel = driverLoginViewModel
                        )
//                        MyScreen(loginViewModel)
                    }


                }
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
//        Log.e("ResumeMain", driverLoginViewModel.listResponse?.size.toString())
        Log.e("ResumeMain", driverLoginViewModel.listResponse.toString())

        Handler().postDelayed({
//            Log.e("ResumeHandleMain", driverLoginViewModel.listResponse?.size.toString())
            Log.e("ResumeHandleMain", driverLoginViewModel.listResponse.toString())

            driverLoginViewModel.a.value = true
        }, 30000)
//        driverLoginViewModel.getRouteList("")
//      Log.e("Resume",driverLoginViewModel.listResponse?.size.toString())
    }
}

@Composable
fun MyScreen(loginViewModel: DriverLoginViewModel,lifecycleOwner: LifecycleOwner) {
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
        "driver", driverLoginViewModel = loginViewModel, startDestination = Routes.Dashboard.name, lifecycleOwner = lifecycleOwner
    )
}

