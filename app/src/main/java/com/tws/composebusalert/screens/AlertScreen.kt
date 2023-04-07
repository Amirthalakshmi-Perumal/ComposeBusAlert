package com.tws.composebusalert.screens

import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tws.composebusalert.nav.Routes

@Composable
fun AlertScreen(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    navController: NavController? = null
) {

    if (showDialog) {
        AlertDialog(
            shape= RoundedCornerShape(5.dp),
            containerColor= Color.White ,
            onDismissRequest = onDismiss,
            title = { Text("ALERT", fontWeight = FontWeight.Normal, textAlign = TextAlign.Left, fontSize = 18.sp) },
            text = { Text("Are you going to pickup or drop the students?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController?.navigate(Routes.MapScreen.name)
//                        onExit()
                    }
                ) {
                    Text("PICKUP")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        navController?.navigate(Routes.MapScreen.name)

//                        onDismiss()

                    }
                ) {
                    Text("DROP")
                }
            }
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUI() {
    val listItems = arrayOf("Favorites", "Options", "Settings", "Share")
    val contextForToast = LocalContext.current.applicationContext

    // state of the menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // remember the selected item
    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    // box
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        // text field
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        // menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // this is a column scope
            // all the items are added vertically
            listItems.forEach { selectedOption ->
                // menu item
                DropdownMenuItem(
                    text = {
                        Text(text = selectedOption)
                    }
                    ,onClick = {
                        selectedItem = selectedOption
                        Toast.makeText(contextForToast, selectedOption, Toast.LENGTH_SHORT).show()
                        expanded = false
                    })
            }
        }
    }
}


