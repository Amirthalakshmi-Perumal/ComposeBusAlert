package com.tws.composebusalert

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tws.composebusalert.data.UserStore
import com.tws.composebusalert.nav.Navigation
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.ui.theme.ComposeBusAlertTheme
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    private val driverLoginViewModel by viewModels<DriverLoginViewModel>()

    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

            val loginViewModel = viewModel(modelClass = DriverLoginViewModel::class.java)
            ComposeBusAlertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val store = UserStore(context)
                    val tokenText = store.getAccessToken.collectAsState(initial = "")

                    var isAuthSuccessful by remember { mutableStateOf(false) }
                    val sharedIdValue = sharedPreferences.getInt("id_key", 0)
                    val sharedNameValue = sharedPreferences.getString("name_key", "defaultname")
                    /* if(sharedIdValue == 0 && sharedNameValue.equals("defaultname")){
                         MyScreen(loginViewModel)

 //                        Navigation(flavor = "driver", startDestination = Routes.DriverSelectRouteScreen.name)
                     }else{
                         MyScreen(loginViewModel)
                     }*/
                    MyScreen(loginViewModel)
                   /* if (sharedIdValue == 22 && sharedNameValue.equals("name")) {
//                        MyScreen(loginViewModel)
                        Navigation(
                            flavor = "driver",
                            startDestination = Routes.DriverSelectRouteScreen.name
                        )
                    } else {
                        MyScreen(loginViewModel)
                    }*/
//                    val sharedPreferences = LocalContext.current.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                    // Check if the user has already authenticated
//                    isAuthSuccessful = sharedPreferences.getBoolean(KEY_AUTHENTICATED, false)

                    /* if (tokenText.value!="") {
                         // User has already authenticated, show the main screen
                        Navigation(flavor = "driver", startDestination = Routes.DriverSelectRouteScreen.name)
                     } else {
 //                        MyScreen(loginViewModel)
                         MyScreen(loginViewModel)
 //                        sharedPreferences.edit().putBoolean(KEY_AUTHENTICATED, true).apply()
                     }*/
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        driverLoginViewModel.getDriverDetailsVM()
    }
}

@Composable
fun MyScreen(loginViewModel: DriverLoginViewModel) {
//    isAuthSuccessful = true
//    sharedPreferences.edit().putBoolean(KEY_AUTHENTICATED, true).apply()
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
        "driver", driverLoginViewModel = loginViewModel, startDestination = Routes.Dashboard.name
    )

}

