package com.tws.composebusalert.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar() {

    Column(

        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp),
//            color = colorResource(id = R.color.purple_200),
            color = Color.Red,
            strokeWidth = Dp(value = 4F)
        )
    }
}