package com.tws.composebusalert.nav


import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.screens.*
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.flow.SharedFlow


@Composable
fun Navigation(
    flavor: String,
    navController: NavHostController = rememberNavController(),
    driverLoginViewModel: DriverLoginViewModel ,
    startDestination: String,
    list:List<RouteListResponse>?=null,
//    lifecycleOwner: LifecycleOwner?=null
    lifecycleOwner: LifecycleOwner,
    context: Context,
//    locationFlow: SharedFlow<Location?>
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}
    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        composable(route = Routes.Dashboard.name) {
            DashBoard(flavor, navController)
        }
        composable(route = Routes.Phone.name) {
            Mobile_Number(navController, driverLoginViewModel)
        }
        composable(route = Routes.OTP.name) {
            OTPScreen(navController, driverLoginViewModel)
        }
        composable(route = Routes.DriverSelectRouteScreen.name) {
           DriverSelectRouteScreen(navController,driverLoginViewModel,lifecycleOwner)
        }
        composable(route = Routes.DriverDashboard.name) {
            DriverDashboard(navController, driverLoginViewModel)
        }
        composable(route = Routes.MapScreen.name ) {
            MapScreen(navController,driverLoginViewModel)
        }
        /* composable(route = Routes.MapScreen.name ) {
            MapScreen(navController,driverLoginViewModel,locationFlow)
        }
        */
    }
}

