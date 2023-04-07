package com.tws.composebusalert.responses

data class LocationDetails(val latitude: Double, val longitude: Double)


/* {
//                    val navController = rememberNavController()
               val map: GoogleMap? = null
               val marker: Marker? = null
               val context = LocalContext.current
               var currentLocation by remember {
                   mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
               }
               fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
               locationCallback = object : LocationCallback() {
                   override fun onLocationResult(p0: LocationResult) {
                       for (lo in p0.locations) {
                           // Update UI with location data
                           currentLocation = LocationDetails(lo.latitude, lo.longitude)
                       }
                   }
               }
               val scope = rememberCoroutineScope()
               val onClick = Navigation(
                   currentLocation.latitude,
                   currentLocation.longitude,
                   map = map,
                   marker = marker
               )
               val launcherMultiplePermissions = rememberLauncherForActivityResult(
                   ActivityResultContracts.RequestMultiplePermissions()
               ) { permissionsMap ->
                   val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                   if (areGranted) {
                       locationRequired = true
                       startLocationUpdates()
                       Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                   } else {
                       Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                   }
               }
               Box(modifier = Modifier.fillMaxSize()) {
                   Column(
                       modifier = Modifier.fillMaxSize().background(Color.LightGray),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center
                   ) {
                       val permissions = arrayOf(
                           Manifest.permission.ACCESS_COARSE_LOCATION,
                           Manifest.permission.ACCESS_FINE_LOCATION
                       )
                       Button(onClick = {
                           if (permissions.all {
                                   ContextCompat.checkSelfPermission(
                                       context, it
                                   ) == PackageManager.PERMISSION_GRANTED
                               }) {

                               startLocationUpdates()
                           } else {
                               launcherMultiplePermissions.launch(permissions)
                           }
                       }) {
                           Text(text = "Get current location")
                       }

                       Text(text = "Latitude : " + currentLocation.latitude)
                       Text(text = "Longitude : " + currentLocation.longitude)
                       if (currentLocation.latitude > 0.0) {
                          Navigation(
                               currentLocation.latitude,
                               currentLocation.longitude,
                               map = map,
                               marker = marker
                           )
//                                Navigation(currentLocation.latitude, currentLocation.longitude)

                       }

//                             Button(onClick = { onClick }) {
//                                 Text("MAP Screen")
//                             }
                   }
               }


           }*/
