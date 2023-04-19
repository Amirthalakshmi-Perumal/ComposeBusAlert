package com.tws.composebusalert.viewmodel


import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.responses.RouteSelectionResponseModel
import com.tws.composebusalert.usecase.AuthUseCase
import com.tws.composebusalert.webservice.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val _routeList = MutableLiveData<List<RouteListResponse>>()
    val routeList: LiveData<List<RouteListResponse>> = _routeList
    fun getRouteList() {
        viewModelScope.launch {
            val response = apiService.getRouteList(
                "524ec4dd-4450-4b6f-8e30-2cfd0ea89e1b", false, "id,name,type"
            )
            _routeList.value = response
        }
    }

    //    var listResponse = MutableLiveData<List<RouteListResponse>>()
    var listResponse: List<RouteListResponse>? = null
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // set the connect timeout to 30 seconds
        .readTimeout(30, TimeUnit.SECONDS).addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZjRmMGRiYTctMTc0MS00YzRjLWI1YzUtNDBkMGJiN2QwMmNiIiwiaWF0IjoxNjgxODg2OTYzLCJleHAiOjE2ODE5NzMzNjN9.9RjU70fZNbKH6v7av8PIP2BBPsEiks8A0XMKQzFlM9E"
            ).build()
            chain.proceed(newRequest)
        }.build()

    val retrofit =
        Retrofit.Builder().baseUrl("http://206.189.137.65/api/v1/profile/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    val apiService = retrofit.create(UserDataSource::class.java)

    val retrofit1: Retrofit =
        Retrofit.Builder().baseUrl("http://206.189.137.65/api/v1/route/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    val apiService1 = retrofit1.create(UserDataSource::class.java)

    private val _routeListResponse = ArrayList<RouteListResponse>()
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
    val a = MutableLiveData<Boolean>()
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
        try {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    responses = apiService1.getRouteList("524ec4dd-4450-4b6f-8e30-2cfd0ea89e1b", false, "id,name,type")
//                    list.value = responses
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
            authUseCase.checkRegisterMobileNumber(
                ctryCode, phone, type, navController, context
            ).collect {
                withContext(Dispatchers.Main) {
                    if (it.data?.name != null) {
                        Log.e("VVVMMWW", "uiuiuiui" + it.data.name.toString())
                        firebaseAuth(navController, context, phone)
                    } else {
                        Log.e("VVVMMWW", "uiuiuiui${it.apiError?.message}")
//                            Log.e("VVVMMWW", "uiuiuiui${it.status}")
//                            Log.e("VVVMMWW", "uiuiuiui${it.data}")
                    }
                }
            }
        }
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
}


