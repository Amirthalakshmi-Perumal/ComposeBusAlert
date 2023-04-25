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
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import com.tws.composebusalert.responses.*
import com.tws.composebusalert.screens.vehicleList
import com.tws.composebusalert.usecase.AuthUseCase
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private val _routeList = MutableLiveData<List<RouteListResponse>>()
    val routeList: LiveData<List<RouteListResponse>> = _routeList
    var locationRequired = false
    var stop1: LatLng? = null
    var stop2: LatLng? = null
    var counter = 0
//    lateinit var a: LatLng

    /*var locationFlow = callbackFlow {
        while (true) {
            ++counter
            val location = newLocation(a)
            Log.d(TAG, "Location $counter: $location")
            trySend(location)
            delay(2_000)
        }
    }.shareIn(
        lifecycleScope, replay = 0, started = SharingStarted.WhileSubscribed()
    )*/
    /*   fun getRouteList() {
           viewModelScope.launch {
               val response = apiService.getRouteList(
                   "524ec4dd-4450-4b6f-8e30-2cfd0ea89e1b", false, "id,name,type"
               )
               _routeList.value = response
           }
       }*/

    //    var listResponse = MutableLiveData<List<RouteListResponse>>()
    var listResponse: List<RouteListResponse>? = null
    var listResponseVehicle: VehicleRouteListResponse? = null
    var vehicleList: ArrayList<VehicleRouteItem>? = null

//    var listResponseVehicle11: ArrayList<VehicleRouteItem> = null
//    val token =
//        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZjRmMGRiYTctMTc0MS00YzRjLWI1YzUtNDBkMGJiN2QwMmNiIiwiaWF0IjoxNjgxODg2OTYzLCJleHAiOjE2ODE5NzMzNjN9.9RjU70fZNbKH6v7av8PIP2BBPsEiks8A0XMKQzFlM9E"

//    val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
var bearToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiY2M3ZDU0Y2UtMTYzMi00YjZlLThhMTMtN2YwMmM5ZDU5OTE5IiwiaWF0IjoxNjgyMzk4MjczLCJleHAiOjE2ODI0ODQ2NzN9.o1s0as41tdGpXJL_Fde-urOsmS5nT4-dqa_DbakK1c8"
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

    private var previouSelectedRouteName: String = ""
    val isFrom = savedStateHandle.get<String>("DriverDashBoard")
    private var previouSelectedRoutePostion: Int? = null

    private val _onSuccess = MutableStateFlow("")
    val onSuccess get() = _onSuccess as StateFlow<String>
    val check = authorizationRepoImpl.onSuccess

    //    private val _isLoading = MutableLiveData<Boolean>()
    private val _driverUserResponse = MediatorLiveData<Profile>()
    val driverUserResponse: LiveData<Profile?> = _driverUserResponse
    private val _groupedRoutesList = ArrayList<RouteSelectionResponseModel>()
    private val _filteredRoute = ArrayList<RouteSelectionResponseModel>()
    val filteredRoute = MutableLiveData<List<RouteSelectionResponseModel>?>()

    //    val isLoading: LiveData<Boolean> = _isLoading.toSingleEvent()
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val _appName = MutableLiveData<String>()
    val appName: LiveData<String> = _appName

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean> = _loggedIn

    private var currentUser: FirebaseUser? = null

    private val _validationError = MutableLiveData<String>()
    val validationError: LiveData<String> = _validationError

    private val _stPhoneNo = MutableLiveData<String>()
    val stPhoneNo: LiveData<String> = _stPhoneNo

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
    val progress: LiveData<Boolean> = _progress

    //    val a = MutableLiveData<Boolean>()
    private val _listData = MutableLiveData<String>()
    val listData: LiveData<String> = _listData


    /*  @RequiresApi(Build.VERSION_CODES.O)
      fun isTokenExpired(token: String): Boolean {
          return try {
              val jwt = Jwts.parser().parseClaimsJwt(token)
              val claims: Claims = jwt.body
              val expiration: Instant = claims["exp"] as Instant
              expiration.isBefore(Instant.now())
          } catch (e: MalformedJwtException) {
              // handle the exception here
              true // or false, depending on how you want to handle the exception
          }
      }
  */
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
//                        val response = apiService.getProfile("d2b5eed8-96a1-4003-8e7b-6571767e969c")
//                        Log.e("Responses", response.createdAt.toString())
                    }
                }
                if (flavor == "driver") {
                    navController?.navigate(Routes.DriverSelectRouteScreen.name)
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
//        obj.check="DashBoard Screen"
        var responses: List<RouteListResponse>? = null
        service = "Route"
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getRouteList(
                        "524ec4dd-4450-4b6f-8e30-2cfd0ea89e1b",
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

    fun getVehicleList(from: String): VehicleRouteListResponse? {
//        obj.check="DashBoard Screen"
        var responses: VehicleRouteListResponse? = null
        try {
//            viewModelScope.launch(Dispatchers.IO) {
                /* val a = withContext(Dispatchers.Default) {
                     apiService.getVehicleList(
                         "7465a78b-817a-4af8-8524-da4485f98b24", "vehicle"
                     )
                 }
                 Log.e("", "With Context $a")
                 listResponseVehicle = a*/
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService.getVehicleList(
                        "7465a78b-817a-4af8-8524-da4485f98b24", "vehicle"
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
//            Log.e("Responses", "New Responses  ${this.listResponseVehicle}")
        } catch (e: Exception) {
            Log.e("VMgetVehicleRouteList", "localizedMessage")
//            setNetworkError(e.localizedMessage)
        }
        Log.e(
            "ResponsesResult",
            " Response VMgetVehicleRouteList ${this.listResponseVehicle.toString()}"
        )
        return listResponseVehicle
    }

    private fun search(query: String) {

        if (!TextUtils.isEmpty(query)) {
            val wanted = _groupedRoutesList.filter {
                it.name.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            }.toList()
            _filteredRoute.clear()
            _filteredRoute.addAll(wanted)
            filteredRoute.value = _filteredRoute
        } else {
            _filteredRoute.clear()
            _filteredRoute.addAll(_groupedRoutesList)
            filteredRoute.value = _filteredRoute
        }
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
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
//                        navController?.navigate(Routes.OTP.name)
                        firebaseAuth(navController, context, phone)
                    } else {
                        Log.e("VVVMMWW2", "uiuiuiui error ${it.apiError?.message}")
                        /* Toast.makeText(
                             context,
                             "This Phone Number is not Verified..",
                             Toast.LENGTH_LONG
                         ).show()*/
//                              navController?.navigate(Routes.Phone.name)
//                            Log.e("VVVMMWW", "uiuiuiui${it.status}")
//                            Log.e("VVVMMWW", "uiuiuiui${it.data}")
                    }
                }
            }
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

    fun updateRouteSelection(postion: Int, selectedRouteName: String) {
        val roulteList: MutableList<RouteSelectionResponseModel> = _filteredRoute
        if (!TextUtils.isEmpty(previouSelectedRouteName)) {
            _groupedRoutesList.forEach { routeResponse ->
                if (routeResponse.name == previouSelectedRouteName) {
                    routeResponse.isChecked = false
                }
            }
        }
        roulteList[postion].isChecked = !roulteList[postion].isChecked
        previouSelectedRoutePostion = postion
        previouSelectedRouteName = selectedRouteName
        filteredRoute.value = roulteList
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


