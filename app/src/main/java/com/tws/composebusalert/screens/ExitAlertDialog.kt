package com.tws.composebusalert.screens

import android.widget.ImageView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ExitAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onExit: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            shape= RoundedCornerShape(5.dp),
            containerColor= Color.White ,
            onDismissRequest = onDismiss,
            title = { Text("STEP ON TIME DRIVER", fontWeight = FontWeight.Normal, fontSize = 18.sp) },
            text = { Text("Are you sure you want to exit the app?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onExit()
                    }
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
