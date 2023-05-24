package com.tws.composebusalert.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.tws.composebusalert.R
import com.tws.composebusalert.nav.Routes


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


@Composable
fun PassengerLogin(navController: NavController? = null) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = "My Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (img, text, btn) = createRefs()
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .constrainAs(img) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(text.top)
                        end.linkTo(parent.end)
                    }
            )
            Text(
                text = "Passenger App",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(text) {
                        start.linkTo(parent.start)
                        top.linkTo(img.bottom, margin = 16.dp)
                        bottom.linkTo(btn.top)
                        end.linkTo(parent.end)
                    }
            )
            Button(
                onClick = {
                    navController?.navigate(Routes.Phone.name)
                },
                modifier = Modifier
                    .constrainAs(btn) {
                        start.linkTo(parent.start)
                        top.linkTo(text.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }, shape = RectangleShape
            ) {
                Text(
                    " Signin with mobile number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                )
            }
        }
    }
}

