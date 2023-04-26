package com.tws.composebusalert.nav


import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
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
    driverLoginViewModel: DriverLoginViewModel,
    startDestination: String,
    lifecycleOwner: LifecycleOwner,
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
        composable(
            route = "A/{arg}",
            arguments = listOf(navArgument("arg") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val argValue = navBackStackEntry.arguments?.getString("arg")
            if (argValue != null) {
                DriverSelectRouteScreen(
                    navController,
                    driverLoginViewModel,
                    lifecycleOwner,
                    argValue
                )
            } else {
                Log.e("asdasdcascasd","Argument value: $argValue")
                Text(text = "Argument value: $argValue")
            }
        }
      /*  composable(route = Routes.DriverDashboard.name) {
            DriverDashboard(navController, driverLoginViewModel, lifecycleOwner)
        }*/
        composable(route = Routes.DriverDashboard.name) {
            DriverDashboard(navController, driverLoginViewModel, lifecycleOwner)
        }
        composable(route = Routes.MapScreen.name) {
            MapScreen(navController, driverLoginViewModel)
        }
    }
}


