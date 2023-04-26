package com.tws.composebusalert.viewmodel


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.location.Location
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.di.Settings
import com.tws.composebusalert.gpstracker.LocationUpdatesService
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import com.tws.composebusalert.request.GeoPositionRequest
import com.tws.composebusalert.request.StartWayPoint
import com.tws.composebusalert.responses.*
import com.tws.composebusalert.screens.vehicleList
import com.tws.composebusalert.usecase.AuthUseCase
import com.tws.composebusalert.util.livedata.toSingleEvent
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.migration.CustomInjection.inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val MINIMUM_DISTANCE = 50
private const val NUMBER_ZERO = 0
private const val ALTERNATE_ROUTE_RANGE = 300
private const val GEOFENCE_RADIUS = 500f
private const val NUMBER_FIFTY = 50
private const val NUMBER_TWENTY = 20

@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val driverDashBoardUseCase: AuthUseCase,
    private val authorizationRepoImpl: AuthorizationRepoImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val firstName = MediatorLiveData<String>().apply {
        addSource(driverUserResponse) {
            value = it?.createdAt.toString()
        }
    }
    private val settings= Settings()
    private var startId: String? = null
    private var routePointsList: MutableList<Point> = mutableListOf()
    private val _polyLineLatLng = MutableLiveData<List<LatLng>>()
    val polyLineLatLng: LiveData<List<LatLng>> = _polyLineLatLng
    private val _stoppingMarkers = MutableLiveData<List<Stoppings>>()
    val stoppingMarkers: LiveData<List<Stoppings>> = _stoppingMarkers
    private val _routeListResponse = ArrayList<RouteListResponse>()
    private val _isStarted = MediatorLiveData<Boolean>()
    val isStarted: LiveData<Boolean> = _isStarted

    private val _startLocationListener = MediatorLiveData<Boolean>()
    private var startLocationServiceResponse: StartLocationServiceResponse? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading.toSingleEvent()
    private var previouSelectedRoutePostion: Int? = null
    private var previouSelectedRouteName: String = ""
    private var smallestDistance: Float = -1f
    private var startWayPoint: Location? = null
    private var endWayPoint: Location? = null
    private var isGeofenceWorking: Boolean = false
    private var count = NUMBER_ZERO
    private var mService: LocationUpdatesService? = null




    var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private val _routeList = MutableLiveData<List<RouteListResponse>>()
    val routeList: LiveData<List<RouteListResponse>> = _routeList
    var locationRequired = false
    var stop1: LatLng? = null
    var stop2: LatLng? = null
    var listResponse: List<RouteListResponse>? = null
    var listResponseVehicle: VehicleRouteListResponse? = null
    var vehicleList: ArrayList<VehicleRouteItem>? = null
    var bearToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiY2M3ZDU0Y2UtMTYzMi00YjZlLThhMTMtN2YwMmM5ZDU5OTE5IiwiaWF0IjoxNjgyNDg1MDYxLCJleHAiOjE2ODI1NzE0NjF9.4ozlVZTnTxlsOk33XSE3vEDuOf24Xqt_72ZqcDPa9lw"
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // set the connect timeout to 30 seconds
        .readTimeout(30, TimeUnit.SECONDS).addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader(
                "Authorization",
                "Bearer $bearToken"
            ).build()
            chain.proceed(newRequest)
        }.build()
    var service = ""
    //    Profile   Route
    val retrofit = Retrofit.Builder()
        .baseUrl(if (service == "Profile") "http://206.189.137.65/api/v1/profile/" else "http://206.189.137.65/api/v1/route/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: UserDataSource = retrofit.create(UserDataSource::class.java)



    private val _onSuccess = MutableStateFlow("")
    val onSuccess get() = _onSuccess as StateFlow<String>
    val check = authorizationRepoImpl.onSuccess

    private val _driverUserResponse = MediatorLiveData<Profile>()
    val driverUserResponse: LiveData<Profile?> = _driverUserResponse
    private val _groupedRoutesList = ArrayList<RouteSelectionResponseModel>()
    private val _filteredRoute = ArrayList<RouteSelectionResponseModel>()
    val filteredRoute = MutableLiveData<List<RouteSelectionResponseModel>?>()


    private val _appName = MutableLiveData<String>()

    private val _loggedIn = MutableLiveData<Boolean>()

    private var currentUser: FirebaseUser? = null

    private val _validationError = MutableLiveData<String>()
    val validationError: LiveData<String> = _validationError

    private val _stPhoneNo = MutableLiveData<String>()
    private var currentLocation: Location? = null
    private var rideType: String? = null
    private var vehicleId: String? = null
    lateinit var phoneNumber: String
    lateinit var oneTime: String
    var checkoneTime: String = "notChecked"
    var btnClick: String = "fail"
    var verificationID = ""
    var message = ""
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance();
    val mAuthUser: FirebaseUser? = mAuth.currentUser
    lateinit var context: Context

    private val _progress = MutableLiveData<Boolean>()

    private val _listData = MutableLiveData<String>()
    val listData: LiveData<String> = _listData

    fun firebaseAuth(
        navController: NavController? = null, context: Context, number: String
    ) {
        this.context = context
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                message = "Verification successful"

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                message = "Fail to verify user : \n" + p0.message
                Log.e("Fail", p0.message.toString())
                Toast.makeText(context, "Verification failed..", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String, p1: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, p1)
                verificationID = verificationId
            }
        }

        sendVerificationCode(number, mAuth, context as Activity, callbacks)
        navController?.navigate(Routes.OTP.name)

    }

    fun checkSuccess(
        navController: NavController? = null,
        flavor: String? = null,
        number: String,
        contexta: Context
    ) {
        if (checkoneTime == "checked") {
            val otp = oneTime
            val user: MutableLiveData<FirebaseUser>? = MutableLiveData()
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                verificationID, otp
            )
            signInWithPhoneAuthCredential(
                credential,
                mAuth,
                user,
                context as Activity,
                contexta,
                message,
                navController,
                flavor,
                number
            )

        } else {
            Toast.makeText(context, "A Please enter otp..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        auth: FirebaseAuth,
        user: MutableLiveData<FirebaseUser>?,
        activity: Activity,
        context: Context,
        mess: String,
        navController: NavController? = null,
        flavor: String? = null,
        number: String
    ): MutableLiveData<FirebaseUser>? {
        val dataStore = StoreData(context)
        auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                message = "Verification successful"
                Log.d("NUMBER_LOGIN_USECASE", "signInWithCredential:success")
                user?.postValue(auth.currentUser)
                viewModelScope.launch(Dispatchers.Main) {
                    if (mAuthUser != null) {
                        authUseCase.registerUserToServer(
                            mAuthUser, LoginType.PHONE_NUMBER, number, context
                        ).apply {
                            if (this != null) {
                                handleRegisterSuccess(mAuthUser)
                                Log.e("VM", "Register User")
                                Log.e("VM", this.token)
                                val storedToken = dataStore.getToken.first()
                                val storedProfileId = dataStore.getProfileId.first()
                                val storedBranchId = dataStore.getBranchId.first()

                                dataStore.saveToken(this.token)
                                dataStore.saveProfileId(this.user.profile?.id.toString())
                                dataStore.saveDriverName(this.user.profile?.name.toString())
                                dataStore.saveImageUrl(this.user.profile?.profilePicURL.toString())
                                dataStore.saveAddress(this.user.profile?.address?.country.toString())

                                dataStore.saveBranchId(this.user.profile?.branch.toString())
                                Log.d("VMstoredToken", "Stored token is $storedToken")
                                Log.d(
                                    "VMstoredProfileId",
                                    "Stored saveProfileId is $storedProfileId"
                                )
                                Log.d("VMstoredBranchId", "Stored saveBranchId is $storedBranchId")
                                if (storedToken != null) {
                                    bearToken=storedToken
                                }
                                /*  if(this.token=="TokenExpiredError"){
                                      authUseCase.registerUserToServer(
                                          mAuthUser, LoginType.PHONE_NUMBER, number, context
                                      )
                                      Log.e("VM",this.token)
                                  }*/
                                Log.e("VM", this.user.profile?.id.toString())

                                Toast.makeText(
                                    context, "Verification successful..", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                _validationError.value = "Test not found"
                                handleFailue()
                            }
                        }
                    }
                }
                if (flavor == "driver") {
                    Log.e("NAVVVVVV","NAJHKGJYTUYTGUYGYHG")
                    navController?.navigate("A/OTP")
//                    navController?.navigate(Routes.DriverSelectRouteScreen.name)
                } else {
                    navController?.navigate(Routes.PassengerList.name)
                }

            } else {

                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        context,
                        "Verification failed.." + (task.exception as FirebaseAuthInvalidCredentialsException).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }.addOnFailureListener {
            Log.e("Verification", "Fail " + it.message)
        }
        return user
    }

    private fun handleFailue() {
        _loggedIn.value = false
        displayProgress(false)
        setNetworkError("Something wrong,Please contact the admin")
    }

    private fun setNetworkError(message: String?) {
        _validationError.value = message!!
        Log.e("DLVM", message)
    }

    private fun handleRegisterSuccess(user: FirebaseUser) {
        currentUser = user
        _loggedIn.value = true
        displayProgress(false)
    }

    private fun displayProgress(progress: Boolean) {
        _progress.value = progress
    }

    private fun sendVerificationCode(
        number: String,
        auth: FirebaseAuth,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91$number")
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(activity).setCallbacks(callbacks).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun getDriverDetailsVM() {
        service = "Profile"
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                val responses = apiService.getProfile("f4f0dba7-1741-4c4c-b5c5-40d0bb7d02cb")
                Log.e("ResponsesDLVM", " responses.createdAt " + responses.createdAt.toString())
                Log.e("DLVM 11111 Responses", listResponse.toString())

            } catch (e: Exception) {

                Log.e("DLVM", "Api call failed")
                Log.e("DLVM", e.message.toString())
//                setNetworkError(e.localizedMessage)
            }
        }
    }

    fun getRouteList(from: String): List<RouteListResponse>? {
        var responses: List<RouteListResponse>? = null
        service = "Route"
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getRouteList(
                        "30f012e9-4a1e-4249-ba5d-992d4ae990a4",
                        false,
                        "id,name,type"
                    )
                    listResponse = responses
                    Log.e(
                        "Responses",
                        "DLVM  Responses" + this@DriverLoginViewModel.listResponse?.size
                    )
                }
            }
            Log.e("Responses", "New Responses  ${this.listResponse}")
        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }
        Log.e("ResponsesResult", " Response ${this.listResponse.toString()}")
        return listResponse
    }
  /*  fun startService(from: String): List<RouteListResponse>? {
        var responses: List<RouteListResponse>? = null
        service = "Route"
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getRouteList(
                        "30f012e9-4a1e-4249-ba5d-992d4ae990a4",
                        false,
                        "id,name,type"
                    )
                    listResponse = responses
                    Log.e(
                        "Responses",
                        "DLVM  Responses" + this@DriverLoginViewModel.listResponse?.size
                    )
                }
            }
            Log.e("Responses", "New Responses  ${this.listResponse}")
        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }
        Log.e("ResponsesResult", " Response ${this.listResponse.toString()}")
        return listResponse
    }
    fun stopService(from: String): List<RouteListResponse>? {
        var responses: List<RouteListResponse>? = null
        service = "Route"
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getRouteList(
                        "30f012e9-4a1e-4249-ba5d-992d4ae990a4",
                        false,
                        "id,name,type"
                    )
                    listResponse = responses
                    Log.e(
                        "Responses",
                        "DLVM  Responses" + this@DriverLoginViewModel.listResponse?.size
                    )
                }
            }
            Log.e("Responses", "New Responses  ${this.listResponse}")
        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }
        Log.e("ResponsesResult", " Response ${this.listResponse.toString()}")
        return listResponse
    }
*/
    @Suppress("ComplexMethod")
    fun startLocationService(rideType: String, vehicleId: String, currentLocation: Location) {
        val routeId: String? = if (rideType == "Pickup") {
            settings.pickupId
        } else {
            settings.dropId
        }
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                routeId?.let { id ->
                    currentLocation.let { location ->
                        driverDashBoardUseCase.startLocationUpdateService(
                            rideType,
                            id,
                            vehicleId,
                            location.latitude,
                            location.longitude
                        )?.let { response ->
                            startId = response.id
                            response.mapDetail?.points?.let { pointlist ->
                                routePointsList.addAll(pointlist)
                            }
                            _startLocationListener.value = true
                            _polyLineLatLng.value =
                                response.mapDetail?.mapData?.routes?.get(0)
                                    ?.overviewPolyline?.points?.let {
                                        driverDashBoardUseCase.decodePoly(
                                            it
                                        )
                                    }
                            _stoppingMarkers.value = response.mapDetail?.stoppings
//                            appLogger.error { startId }

                            response.latitude = location.latitude
                            response.longitude = location.longitude
                            updateLocationToApi(location, null, 0.0,true)
                            startLocationServiceResponse = response
                            _isStarted.value = true
                        }
                    }
                }
            } catch (e: Exception) {
//                appLogger.error(e) {
//                    e.localizedMessage
//                }
                startId = null
                _isStarted.value = false
                setNetworkError(e.localizedMessage)
            }
        }
    }
    private suspend fun updateLocationToApi(
        location: Location,
        startPoint: Location?,
        movementLength: Double,
        showProgress: Boolean
    ) {
        try {
            startId?.let {
//                _showProgress.value = showProgress
                _isLoading.value = true
                driverDashBoardUseCase.updateGeoPosition(
                    GeoPositionRequest(
                        location.latitude,
                        location.longitude,
                        it,
                        StartWayPoint(startPoint?.latitude, startPoint?.longitude),
                        movementLength
                    )
                )
                _isLoading.value = false
//                _showProgress.value = false
            }
        } catch (e: Exception) {
            _isLoading.value = false
            e.printStackTrace()
        }
    }
    private fun startTrackerService(isFor: String) {

        if (isFor == "forStart") {
            currentLocation = mService?.lastLocation
            updateDriverRideType(rideType, vehicleId)
            stopService()
        } else {
            mService?.requestLocationUpdates()
        }
    }
    private fun stopService() {
        mService?.removeLocationUpdates()
    }

    private fun updateDriverRideType(it: String?, vehicleId: String?) {
        if (it == "Pickup") {
            currentLocation?.let { it1 ->
                startLocationService(
                  "Pickup", vehicleId!!,
                    it1
                )
            }
        } else {
            currentLocation?.let { it1 ->
               startLocationService(
                    "Drop", vehicleId!!,
                    it1
                )
            }
        }
    }
    fun stopLocationUpdate(activityId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                driverDashBoardUseCase.stopLocationUpdate(activityId).let {
                    startId = null
                    startWayPoint = null
                    endWayPoint = null
                    smallestDistance = -1f
                    isGeofenceWorking = false
                    count = 0
                    routePointsList.clear()
                    _startLocationListener.value = false
                    _isStarted.value = false
                }
            } catch (e: Exception) {
//                appLogger.error(e) {
//                    e.localizedMessage
//                }
                setNetworkError(e.localizedMessage)
            }
        }
    }



/*    fun storeRouteDetails() {
        var selectedRoute: String? = null
        _filteredRoute.forEach {
            if (it.isChecked) {
                selectedRoute = it.name
            }
        }

        for (routeDetails in _routeListResponse) {
            if (routeDetails.name == selectedRoute) {
                driverDashBoardUseCase.storeRouteDetails(routeDetails.type, routeDetails)
            }
        }
    }*/
    fun getVehicleList(from: String): VehicleRouteListResponse? {
        var responses: VehicleRouteListResponse? = null
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getVehicleList(
                        "2835693b-736d-4275-a21f-628c3e5f7208", "vehicle"
                    )
                    listResponseVehicle = responses

                    vehicleList = responses
                    stop1 =
                        vehicleList?.get(0)?.startPoint?.latitude?.let {
                            vehicleList?.get(0)?.startPoint?.longitude?.let { it1 ->
                                LatLng(
                                    it,
                                    it1
                                )
                            }
                        }
                    stop2 =
                        vehicleList?.get(0)?.endPoint?.latitude?.let {
                            vehicleList?.get(0)?.endPoint?.longitude?.let { it1 ->
                                LatLng(
                                    it,
                                    it1
                                )
                            }
                        }
                }

                Log.e(
                    "Responses",
                    " Vehicle responses DLVM  Responses" + this@DriverLoginViewModel.listResponseVehicle?.size
                )
            }
        } catch (e: Exception) {
            Log.e("VMgetVehicleRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }
        Log.e(
            "ResponsesResult",
            " Response VMgetVehicleRouteList ${this.listResponseVehicle.toString()}"
        )
        return listResponseVehicle
    }

    fun signIn(
        ctryCode: String,
        phone: String,
        type: String,
        navController: NavController? = null,
        context: Context,
    ) {
        navController?.navigate(Routes.OTP.name)
        getDriverDetailsVM()
        viewModelScope.launch(Dispatchers.IO) {
            /* if (isTokenExpired(token)) {
                 println("Token has expired.")
             } else {
                 println("Token is still valid.")
             }*/
            authUseCase.checkRegisterMobileNumber(
                ctryCode, phone, type, navController, context
            ).collect {
                withContext(Dispatchers.Main) {
                    if (it.data?.name != null) {
                        Log.e("VVVMMWW1", "uiuiuiui " + it.data.name.toString())
                        firebaseAuth(navController, context, phone)
                    } else{
//                        Toast.makeText(context,"Not Registered Number",Toast.LENGTH_LONG).show()
                        Log.e("VVVMMWW33", "uiuiuiui 3333error ${it.apiError?.message}")
//                        navController?.navigate(Routes.Phone.name)
                    }
                   /* else if( it.data?.name == " "){
                        Toast.makeText(context,"Not Registered Number",Toast.LENGTH_LONG).show()
                        Log.e("VVVMMWW2", "uiuiuiui 222error ${it.apiError?.message}")
                        navController?.navigate(Routes.Phone.name)
                    }else{
                        Toast.makeText(context,"Not Registered Number",Toast.LENGTH_LONG).show()
                        Log.e("VVVMMWW33", "uiuiuiui 3333error ${it.apiError?.message}")
                        navController?.navigate(Routes.Phone.name)
                    }*/
                }
            }
        }
    }

    @Composable
    fun Snackbars() {
        Snackbar(modifier = Modifier.padding(4.dp)) {
            Text(text = "Not Registered Number")
        }
    }

    suspend fun signOut(navController: NavController? = null, context: Context,) {
        val dataStore = StoreData(context)
        val storedToken = dataStore.getToken.first()
        FirebaseAuth.getInstance().signOut()
        dataStore.clearData()
        Log.d("VMstoredToken After Clear", "Cleared token is $storedToken")
        navController?.navigate(Routes.Dashboard.name)
    }


    //    MapActivity Starts here
    fun checkLocationSetting(
        context: Context, onDisabled: (IntentSenderRequest) -> Unit, onEnabled: () -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(true).setMinUpdateIntervalMillis(50)
            .setMaxUpdateDelayMillis(100).build()

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }
    }

    fun newLocation(a: LatLng): Location {
        val location = Location("MyLocationProvider")
        location.apply {
            latitude = a.latitude
            longitude = a.longitude
        }
        return location
    }

    fun bitmapDescriptorFromVector(
        context: Context, vectorResId: Int
    ): BitmapDescriptor? {

        // retrieve the actual drawable
        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        drawable.setBounds(0, 0, 120, 120)
        val bm = Bitmap.createBitmap(
            120, 120, Bitmap.Config.ARGB_8888
        )
        // draw it onto the bitmap
        val canvas = android.graphics.Canvas(bm)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest, it, Looper.getMainLooper()
            )
        }
    }

}


