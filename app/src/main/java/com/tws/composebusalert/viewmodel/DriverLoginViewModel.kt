package com.tws.composebusalert.viewmodel


import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.firebase.auth.FirebaseUser
import com.tws.composebusalert.util.livedata.toSingleEvent
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.nav.Routes
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.usecase.AuthUseCase
import com.tws.composebusalert.webservice.UserDataSource
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess


@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val authorizationRepoImpl: AuthorizationRepoImpl,
) : ViewModel() {
    val firstName = MediatorLiveData<String>().apply {
        addSource(driverUserResponse) {
            value = it?.createdAt.toString()
        }
    }

    /* val address = MediatorLiveData<String>().apply {
         addSource(driverUserResponse) {
             it?.profilePicURL?.let { url ->
                 value = url
             }
         }
     }*/
    val client = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZDJiNWVlZDgtOTZhMS00MDAzLThlN2ItNjU3MTc2N2U5NjljIiwiaWF0IjoxNjgwODUyOTM3LCJleHAiOjE2ODA5MzkzMzd9.ntlzuhZ-AwQscIYgDVWMVs2SRM3h-ABSMcJ8vN9E3UY"
                ).build()
            chain.proceed(newRequest)
        }.build()
    val retrofit =
        Retrofit.Builder().baseUrl("http://206.189.137.65/api/v1/profile/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    val apiService = retrofit.create(UserDataSource::class.java)

//    val response = apiService.getProfile()

    private val _onSuccess = MutableStateFlow("")
    val onSuccess get() = _onSuccess as StateFlow<String>
    val check = authorizationRepoImpl.onSuccess
    private val _isLoading = MutableLiveData<Boolean>()
    private val _driverUserResponse = MediatorLiveData<Profile>()
    val driverUserResponse: LiveData<Profile?> = _driverUserResponse

    val isLoading: LiveData<Boolean> = _isLoading.toSingleEvent()
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val _appName = MutableLiveData<String>()
    val appName: LiveData<String> = _appName

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean> = _loggedIn

    private var currentUser: FirebaseUser? = null

    private var loginType: LoginType = LoginType.GOOGLE
    val TAG = "Bus-App"


    /**
     * To kill the app if user press the back button
     * */

    fun killApp() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(1)
    }

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


    fun firebaseAuth(
        navController: NavController? = null, context: Context, number: String
    ) {
//        var isAuthSuccessful by remember { mutableStateOf(false) }
//        val PREFS_FILENAME = "com.example.myapp.prefs"
//        val KEY_AUTHENTICATED = "authenticated"
//        val sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        this.context = context
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                message = "Verification successful"
//                Toast.makeText(context, "Verification successful..", Toast.LENGTH_SHORT).show()
//                sharedPreferences.edit().putBoolean(KEY_AUTHENTICATED, true).apply()
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

    fun checkSuccess(navController: NavController? = null, flavor: String? = null, number: String) {
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
                context,
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
        var message = mess

        auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                message = "Verification successful"
                Log.d("NUMBER_LOGIN_USECASE", "signInWithCredential:success")
                user?.postValue(auth.currentUser)
                viewModelScope.launch(Dispatchers.Main) {
                    if (mAuthUser != null) {
                        authUseCase.registerUserToServer(
                            mAuthUser, LoginType.PHONE_NUMBER, number
                        ).apply {
                            if (this != null) {
                                _isLoading.value = false
                                handleRegisterSuccess(mAuthUser)
                                Toast.makeText(
                                    context, "Verification successful..", Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                _isLoading.value = false
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

    /**
     * This method is returned currently logged in user profile
     * */
    fun getDriverDetailsVM() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
            Log.e("Responses", "response.createdAt.toString()")
            val responses = apiService.getProfile("f4f0dba7-1741-4c4c-b5c5-40d0bb7d02cb")
            Log.e("Responses", "gshtrstrdhtgfdtytrfjyt  "+responses.createdAt.toString())



//                val response = authUseCase.getDriverDetails()
//                _isLoading.value = false
//                _driverUserResponse.value = response!!

            } catch (e: Exception) {
//                Log.e("DLVM", "Api call failed")

//                _isLoading.value = false
//                appLogger.error(e) {
//                    "Api call failed"

                Log.e("DLVM", "Api call failed")
                Log.e("DLVM", e.message.toString())
//                setNetworkError(e.localizedMessage)
            }
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
}


