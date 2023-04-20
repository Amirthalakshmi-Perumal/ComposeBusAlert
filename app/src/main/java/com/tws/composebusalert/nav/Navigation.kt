package com.tws.composebusalert.nav


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.screens.*
import com.tws.composebusalert.viewmodel.DriverLoginViewModel




@Composable
fun Navigation(
    flavor: String,
    navController: NavHostController = rememberNavController(),
    driverLoginViewModel: DriverLoginViewModel ,
    startDestination: String,
    list:List<RouteListResponse>?=null,
//    lifecycleOwner: LifecycleOwner?=null
    lifecycleOwner: LifecycleOwner
) {
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
        composable(route = Routes.MapScreen.name) {
            MapScreen(navController,driverLoginViewModel)
        }

    }
}

