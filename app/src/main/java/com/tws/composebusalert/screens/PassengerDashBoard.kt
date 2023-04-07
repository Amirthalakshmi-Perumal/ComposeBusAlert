package com.tws.composebusalert.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.tws.composebusalert.R


@Composable
fun PassengerDashBoard(navController: NavController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_login), contentDescription = "My Image",
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = null)
    }
}