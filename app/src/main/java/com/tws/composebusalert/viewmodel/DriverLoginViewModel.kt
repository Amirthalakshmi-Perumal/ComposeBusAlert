package com.tws.composebusalert.viewmodel


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.tws.composebusalert.R
import com.tws.composebusalert.datastore.StoreData
import com.tws.composebusalert.di.SERVER_URL
import com.tws.composebusalert.di.Settings
import com.tws.composebusalert.gpstracker.LocationUpdatesService
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.network.NetworkAuthenticator
import com.tws.composebusalert.network.ResponseHandler
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import com.tws.composebusalert.request.GeoPositionRequest
import com.tws.composebusalert.request.PassengerDetailRequest
import com.tws.composebusalert.request.StartLocationServiceRequest
import com.tws.composebusalert.request.StartWayPoint
import com.tws.composebusalert.request.StopLocationUpdateRequest
import com.tws.composebusalert.responses.*
import com.tws.composebusalert.usecase.AuthUseCase
import com.tws.composebusalert.util.livedata.toSingleEvent
import com.tws.composebusalert.webservice.AppSettingDataSource
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val NUMBER_ZERO = 0
private const val ANCHORING_VALUE: Float = 0.5f
private var startId: String? = null

@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val driverDashBoardUseCase: AuthUseCase,
    private val authorizationRepoImpl: AuthorizationRepoImpl,
    savedStateHandle: SavedStateHandle,
//    @ApplicationContext applicationContext: ApplicationContext,
) : ViewModel() {

    var res = MutableLiveData<List<PassengerDetailResponse>>()

    val firstName = MediatorLiveData<String>().apply {
        addSource(driverUserResponse) {
            value = it?.createdAt.toString()
        }
    }
    val responseHandler = ResponseHandler()

    var q = MutableStateFlow(false)
    val w: StateFlow<Boolean> = q.asStateFlow()

    private val settings = Settings()
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
    private var mBound = false
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var isDriverStarted: Boolean = false

    private val _studentLocationResponse = MutableLiveData<StartLocationServiceResponse>()
    val studentLocationResponse: LiveData<StartLocationServiceResponse> =
        _studentLocationResponse.toSingleEvent()

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

    var emtList = ""

    var bearToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiODExYjA4ZjgtYTg5Zi00NmY5LWJlMzgtNTYzOWZhYzlkOGVmIiwiaWF0IjoxNjg2NjMzMjA0LCJleHAiOjE2ODY3MTk2MDR9.X_eI1VXUs-BDB6N_34dZ8krc9GcxOwWclMebLe291oQ"


    /*  val client = OkHttpClient.Builder()
          .connectTimeout(30, TimeUnit.SECONDS) // set the connect timeout to 30 seconds
          .readTimeout(30, TimeUnit.SECONDS).addInterceptor { chain ->
              val newRequest = chain.request().newBuilder().addHeader(
                  "Authorization",
                  "Bearer ${refreshToken()}"
              ).build()
              chain.proceed(newRequest)
          }.build()*/

    private fun provideClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).addInterceptor { chain ->
                val newRequest = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer $bearToken"
                ).build()
                //                    Log.e("ZZZZ",newRequest.url.toString())
                chain.proceed(newRequest)
            }.addInterceptor(interceptor)

        return client.authenticator(NetworkAuthenticator(createAppSettingWebService(client.build())))
            .build()
    }

    var service = ""

    private fun createAppSettingWebService(okHttpClient: OkHttpClient): AppSettingDataSource {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()
        return retrofit.create(AppSettingDataSource::class.java)
    }

    //    Profile   Route
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(
            "http://206.189.137.65"
        )
        .client(provideClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val apiService: UserDataSource = retrofit.create(UserDataSource::class.java)

    private val _onSuccess = MutableStateFlow("")
    val onSuccess get() = _onSuccess as StateFlow<String>
    val check = authorizationRepoImpl.onSuccess

    private val _driverUserResponse = MediatorLiveData<Profile>()
    val driverUserResponse: LiveData<Profile?> = _driverUserResponse

    private val _loggedIn = MutableLiveData<Boolean>()

    private var currentUser: FirebaseUser? = null

    private val _validationError = MutableLiveData<String>()
    val validationError: LiveData<String> = _validationError

    //    private var currentLocation: Location? = null
    private var currentLocation = Location("dummyProvider")

    //    private var currentLocation = Location(LatLng(11.930390, 79.807510))
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
//            justForToken(contexta)
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
                                Log.e("VMcaretaker", "caretaker" + this.caretaker?.id.toString())

                                dataStore.saveCareTakerId(this.caretaker?.id.toString())
//                                dataStore.saveToken(this.token)
//                                val storedToken = dataStore.getToken.first()
//                                if (storedToken != null && storedToken == "") {
//                                    bearToken = storedToken
//                                }
//                                Log.d("VMstoredToken", "Stored token is $storedToken")
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
                    Log.e("NAVVVVVV", "NAJHKGJYTUYTGUYGYHG")
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


    fun getDriverDetailsVM(context: Context) {
        service = "Profile"
        val dataStore = StoreData(context)
        viewModelScope.launch(Dispatchers.Main.immediate) {
            val storedProfileId = dataStore.getProfileId.first()

            try {
                var responses = apiService.getProfile("f4f0dba7-1741-4c4c-b5c5-40d0bb7d02cb")

                if (storedProfileId != null) {
                    responses = apiService.getProfile(storedProfileId)
                }
                Log.e("ResponsesDLVM", " responses.createdAt " + responses.createdAt.toString())
                Log.e("DLVM 11111 Responses", listResponse.toString())

            } catch (e: Exception) {
//                Log.e("DLVM", "Api call failed")
//                Log.e("DLVM", e.message.toString())
//                setNetworkError(e.localizedMessage)
            }
        }
    }

    fun getRouteList(context: Context): List<RouteListResponse>? {
        val dataStore = StoreData(context)
        var responses: List<RouteListResponse>? = null
        service = "Route"
        try {
            viewModelScope.launch {
                val storedBranchId = dataStore.getBranchId.first()
                withContext(Dispatchers.Main) {
                    responses = apiService.getRouteList(
                        "30f012e9-4a1e-4249-ba5d-992d4ae990a4",
//                        storedBranchId,
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

    fun startService(rideType: String, context: Context, navController: NavController?) {
        var responses: StartLocationServiceResponse
        service = "startService"
        val dataStore = StoreData(context)
        currentLocation.latitude = 11.930390
        currentLocation.longitude = 79.807510
        try {
            viewModelScope.launch {
                val routeId: String = if (rideType == "Pickup") {
                    dataStore.getPickUpId.first()
                } else {
                    dataStore.getDropId.first()
                }
                val f = dataStore.getStartServiceId.first()
                Log.d("savedStartService", "savedStartServiceAAAAA $f")
                Log.d(
                    "savedStartServicegetPickUpId",
                    "savedStartService ${dataStore.getPickUpId.first()}"
                )
                Log.d(
                    "savedStartServicegetDropId",
                    "savedStartService ${dataStore.getDropId.first()}"
                )
                val vehicleId = dataStore.getVehicleId.first()
                Log.d("savedStartServicevehicleId", "vehicleId $vehicleId")
                Log.d("savedStartServicevehicleId", "rideType $rideType")

                withContext(Dispatchers.Main) {
                    val obj = StartLocationServiceRequest(
                        rideType, routeId,
                        vehicleId!!, currentLocation.latitude, currentLocation.longitude
                    )
                    try {
                        responses = apiService.startLocationService(obj)
                        Log.d("rrrr", "responses $responses")
                        responses.id?.let {
                            startId = responses.id
                            dataStore.saveStartService(it)
                            Log.d("DDDRR", "responses $it")
                            Log.d("DDDRR", "responses ${responses.toString()}")
                            val a = listOf(
                                StoppingListDS(11.93702, 79.80877),
                                StoppingListDS(11.92442, 79.80949),
                                StoppingListDS(11.91877, 79.81569)
                            )
                            dataStore.saveStoppings(a)
//                          navController?.navigate(Routes.MapScreen.name) {
//                              launchSingleTop = true
//                          }
                        } ?: Log.d("rrrr", "startService")

                        responseHandler.handleSuccess(responses)
                    } catch (e: Exception) {
                        responseHandler.handleException(e)
                    }

                }
            }

        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
//            setNetworkError(e.localizedMessage)
            Toast.makeText(context, "Start service not Ended", Toast.LENGTH_SHORT).show()

        }
        Log.e("ResponsesResult", " Response ${this.listResponse.toString()}")

    }

    fun endService(context: Context) {
        service = "endService"
        var responses: StartLocationServiceResponse? = null
        val dataStore = StoreData(context)
        try {
            viewModelScope.launch {

                val activity = dataStore.getStartServiceId.first()

//                withContext(Dispatchers.Main) {
                val obj = StopLocationUpdateRequest(activity)
//                    responses = apiService.stopLocationService(obj)
                apiService.stopLocationService(obj)
//                }
            }

        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }
    }
    fun updateGeoLocation(
        location: LocationDetails,
        startPoint: Location?,
        movementLength: Double,
        showProgress: Boolean
    )
    {
        var responses:GeoPositionResponse?=null
        try {
            viewModelScope.launch {

                Log.e("9999", "b4444")

               /* responses =  apiService.updateGeoLocation(
                    GeoPositionRequest(
                        11.9305882531181,
                        79.79175288768954,
                        "a3686d1f-4129-40a6-9f38-57a6ed150843",
                        StartWayPoint(11.93056, 79.79175),
                        3.1572723388671875
                    )
                )*/
                try {
                    startId?.let {
                        responses =   apiService.updateGeoLocation(
                            GeoPositionRequest(
                                location.latitude,
                                location.longitude,
//                                "a3686d1f-4129-40a6-9f38-57a6ed150843",
                                it,
                                StartWayPoint(startPoint?.latitude, startPoint?.longitude),
                                movementLength
                            )
                        )
                        Log.e("99999", "Attter")
                        Log.e("99999", responses.toString())
//                Log.e("99999", responses!!.id.toString())

                        _isLoading.value = false
//                _showProgress.value = false
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            Log.e("99999", responses.toString())
            Log.e("99999 VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
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


    fun placeMarkersOnMap(it: List<Stoppings>?) {
        it?.let { stoppingList ->
            stoppingList.forEach {
                mMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .anchor(ANCHORING_VALUE, ANCHORING_VALUE)
                        .title(it.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
                )
            }
        }
    }

    suspend fun getVehicleList(from: String, context: Context): VehicleRouteListResponse? {
        var responses: VehicleRouteListResponse? = null
        var routeId: String? = null
        val dataStore = StoreData(context)
        var storedVehicleId: String? = null
        val pickUpId = dataStore.getPickUpId.first()
        val dropId = dataStore.getDropId.first()

        Log.d("BBBB", "Stored pickUpId is $pickUpId")
        Log.d("BBBB", "Stored dropId is $dropId")
        if (from == "PICKUP") {
            routeId = pickUpId
        }
        if (from == "DROP") {
            routeId = dropId

        }
        if (routeId != null) {
            Log.d("BBBB", "Stored routeId  which is by if  is $routeId")
            try {
                withContext(Dispatchers.Main) {
                    Log.e("AAA ", "B4")
                    responses = apiService.getVehicleList(routeId, "vehicle")
                    Log.e("AAA ", "After   ${responses.toString()}")
                    if (responses != null) {
                        Log.e("AAA ", "Res not null")
                        if (responses!![0].vehicle.isEmpty()) {
                            dataStore.saveVehicleId("")
                            emtList = "List Not Found"
                            Log.e("AAA ", "Res not null nut List Not Found ")
                            listResponseVehicle = null

                            Log.e("EMT ", emtList)
                        } else {
                            listResponseVehicle = responses
                            vehicleList = responses
                            dataStore.saveVehicleId(responses!![0].vehicle[0].id)
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
                    } else {
                        Toast.makeText(context, "Vehicle List Fails", Toast.LENGTH_SHORT).show()

                        Log.e("AAA  not null", "Res not null")

                    }
                }
                viewModelScope.launch {
                    storedVehicleId = dataStore.getVehicleId.first()
                    Log.e("VMstoredVehicleId", "storedVehicleId  $storedVehicleId")
                }

            } catch (e: Exception) {
                Log.e("VMgetVehicleRouteList", "localizedMessage")
                setNetworkError(e.localizedMessage)
            }
        } else {
            Toast.makeText(context, "Vehicle List Fails", Toast.LENGTH_SHORT).show()
            Log.e("VehicleGGGGGGGG", "  Vehicle List Fails")
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
        val dataStore = StoreData(context)
        navController?.navigate(Routes.OTP.name)
        getDriverDetailsVM(context)
        viewModelScope.launch(Dispatchers.IO) {

            authUseCase.checkRegisterMobileNumber(
                ctryCode, phone, type, navController, context
            ).collect {

                withContext(Dispatchers.Main) {
                    if (it.data?.name != null) {
                        Log.e("VVVMMWW1", "uiuiuiui " + it.data.name.toString())
                        firebaseAuth(navController, context, phone)
                        dataStore.saveDriverName(it.data.name)
                        dataStore.saveProfileId(it.data.id.toString())
//                      dataStore.saveDriverName(this.user.profile?.name.toString())
                        dataStore.saveImageUrl(it.data.profilePicURL.toString())
                        dataStore.saveAddress(it.data.address?.country.toString())

                        dataStore.saveBranchId(it.data.branch.toString())

                        val storedProfileId = dataStore.getProfileId.first()
                        val storedBranchId = dataStore.getBranchId.first()
                        Log.d(
                            "VMstoredProfileId",
                            "Stored saveProfileId is $storedProfileId"
                        )
                        Log.d("VMstoredBranchId", "Stored saveBranchId is $storedBranchId")
//                            if (storedToken != null) {
//                                bearToken = storedToken
//                            }
                        /*  if(this.token=="TokenExpiredError"){
                              authUseCase.registerUserToServer(
                                  mAuthUser, LoginType.PHONE_NUMBER, number, context
                              )
                              Log.e("VM",this.token)
                          }*/
                        Log.e("VM", "CheckMobileNumberResponse   " + it.data.toString())

                        /*  var ana=   authUseCase.registerUserToServer(
                                 mAuthUser, LoginType.PHONE_NUMBER, phone, context
                             )
                             Log.e("VMVMana",ana.toString())
                             if (ana != null) {
                                 Log.e("VMVMana",ana.token)
                                 dataStore.saveToken(ana.token)

                             }*/

//                            Log.d("VMstoredToken", "Stored token is $storedToken")

//                            Log.e("VM",this.token)

                    } else {
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

    /* fun justForToken(
         context: Context,
     ) {
         val dataStore = StoreData(context)

         viewModelScope.launch(Dispatchers.IO) {
             val storedToken = dataStore.getToken.first()
             val storedphone = "8870068009"

             if (storedphone != "") {
                 val ana = authUseCase.registerUserToServer(
                     mAuthUser, LoginType.PHONE_NUMBER, storedphone, context
                 )
                 Log.e("VMVMana", ana.toString())
                 if (ana != null) {
                     Log.e("VMVMana", ana.token)
                     dataStore.saveToken(ana.token)
                 }
             }
             if (storedToken != null && storedToken != "") {
                 bearToken = storedToken
             }
             Log.d("VMstoredToken", "Stored token is $storedToken")
             //                            Log.e("VM",this.token)
         }
     }*/

    fun getPassengersDetail(context: Context): List<PassengerDetailResponse>? {
        val dataStore = StoreData(context)
        var responses: List<PassengerDetailResponse>? = null
        service = "PassengersDetail"
        try {
            viewModelScope.launch {
                val storedNo = dataStore.getNo.first()
                val obj = PassengerDetailRequest("+91", "8667858046")
                withContext(Dispatchers.Main) {
                    res.value = apiService.getPassengersDetail(obj, false)
                    Log.e("ResponsesRRR", "New Responses  ${responses}")
                }
            }

        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
            setNetworkError(e.localizedMessage)
        }

        Log.e("ResponsesRRR", " Response ${responses}")
        return responses
    }


        fun getCurrentBusLocation() {

            service = "getCurrentBusLocation"
            var responses: DriverCurrentLocationResponse? = null

            try {
                viewModelScope.launch(Dispatchers.Main) {
                    responses =apiService.getCurrentBusLocation("a3686d1f-4129-40a6-9f38-57a6ed150843")
                    withContext(Dispatchers.Main) {
                        Log.d("888", "responses $responses")
                        if (responses != null && !TextUtils.isEmpty(responses!!.activity.toString())) {

                            Log.d("8888", "responses activity ${responses!!.activity.toString()}")
                            Log.d("888", "responses chh $responses")

                        } else {
                            Log.d("888", "response  Bus null  $responses")

                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("Exception", "localizedMessage")
                setNetworkError(e.localizedMessage)
            }
        }

        fun getStudentRoute(studentId: String): String {
        var responses: StartLocationServiceResponse? = null
//        setProgress(true)
        var toCheck = ""
        var start = ""
        var end = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start = calculateDate(true)
            end = calculateDate(false)
        }
            Log.e("studentId", "responses $studentId")

            _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                responses = apiService.getStudentRoute(
//                    "4d9d94ab-c73d-4ea6-8fa2-3cfc3f43b87f",
                    studentId,
                    start,
                    end
                )
                Log.e("responses", "responses $responses")
                     withContext(Dispatchers.Main) {
                         Log.d("777", "responses $responses")
                         if (responses != null && !TextUtils.isEmpty(responses!!.id)) {
                             toCheck = "R"
                             responses.let { _studentLocationResponse.value = it }

                             Log.d("777SS", "responses IIDD ${responses!!.id.toString()}")
                             Log.d("777", "responses chh $responses")

                         } else {
                             toCheck = "F"
                             Log.d("777", "response  Bus not started yet $responses")

                             _validationError.value = "Bus not started yet"
                         }
                     }
//                        responseHandler.handleSuccess(responses)
            } catch (e: Exception) {
                toCheck = "F"
                Log.e("777VMgetRouteListwer", "localizedMessage ${e.toString()}")
//                        responseHandler.handleException(e)
            }

        }

  /*      try {


        } catch (e: Exception) {
            Log.e("777VMgetRouteList", "localizedMessage")
//            setNetworkError(e.localizedMessage)
            Toast.makeText(context, "Start service not Ended", Toast.LENGTH_SHORT).show()
//            toCheck = "F"
        }*/
        Log.e("777ResponsesResult", " Responselll ${responses.toString()}")


        return toCheck
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDate(isStart: Boolean): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDate = LocalDate.now()   // your current date time
        val startOfDay: LocalDateTime = localDate.atStartOfDay() // date time at start of the date
        val endOfDate: LocalDateTime =
            localDate.atTime(LocalTime.MAX) // date time at end of the date
        val timestamp = startOfDay.atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli() // start time to timestamp
        var returnVal: String = ""
        returnVal = if (isStart) {
            startOfDay.format(dateFormatter)
        } else {
            endOfDate.format(dateFormatter)
        }
        return returnVal
    }

    private fun refreshToken(): String {

        try {
//            viewModelScope.launch {
//                val storedNo = dataStore.getNo.first()
//                withContext(Dispatchers.Main) {
            bearToken = apiService.getRefreshToken()
            Log.e("ResponsesRRR", "New bearToken  ${bearToken}")
//                }
//            }

        } catch (e: Exception) {
            Log.e("VMgetRouteList", "localizedMessage")
//            setNetworkError(e.localizedMessage)
        }

        return bearToken
    }


    suspend fun signOut(navController: NavController? = null, context: Context) {
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

/*
override fun authenticate(route: Route?, response: Response): Request? {
    var refreshToken: String? = settings.token
    try {
        refreshToken = appSettingDataSource.refreshToken().blockingGet().token
    } catch (e: Exception) {
        e.printStackTrace()
    }

    settings.token = refreshToken
    refreshToken?.let { preferenceManager.storeAndUpdateAccessToken(it) }
    return response.request.newBuilder()
        .header("Authorization", String.format("Bearer %s", settings.token))
        .build()
    */




fun refreshBearerToken(refreshToken: String): String? {

    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()


    val requestBody = FormBody.Builder()
//        .add("grant_type", "refresh_token")
        .add("refresh_token", refreshToken)
        .build()

    val request = Request.Builder()
        .url("http://206.189.137.65/api/v1/refreshToken")
//        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()

    if (response.isSuccessful && responseBody != null) {
        val tokenResponse = Gson().fromJson(responseBody, TokenResponse::class.java)
        return tokenResponse.accessToken
    }
    return null
}

data class TokenResponse(val accessToken: String)

