package com.tws.composebusalert.repo.impl

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.firebase.auth.*
import com.tws.composebusalert.applyCommonSideEffects
import com.tws.composebusalert.exception.ErrorHandler
import com.tws.composebusalert.exception.ErrorHandler.handleException
import com.tws.composebusalert.repo.AuthorizationRepo
import com.tws.composebusalert.request.CheckMobileNumberRequest
import com.tws.composebusalert.request.UserData
import com.tws.composebusalert.responses.CheckMobileNumberResponse
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.responses.UserRegisterResponse
import com.tws.composebusalert.services.ApiFailureException
import com.tws.composebusalert.services.BusAlertException
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceBus
import com.tws.composebusalert.webservice.UserDataSource
import com.tws.composebusalert.network.ResponseHandler
import com.tws.composebusalert.responses.RouteListResponse
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.lang.Exception

class AuthorizationRepoImpl @Inject constructor(
    private val userDataSource: UserDataSource,
//    private val authorizationRepo: AuthorizationRepo,

//    private val firebaseAuth: FirebaseAuth,
) : AuthorizationRepo {
    lateinit var oneTime: String
    val loginViewModel: DriverLoginViewModel? = null
    var checkoneTime: String = "notChecked"
    var btnClick: String = "fail"
    var verificationID = ""
    var message = ""
    val responseHandler = ResponseHandler()
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance();
    lateinit var context: Context
    private val _onSuccess = MutableStateFlow("")
    val onSuccess get() = _onSuccess as StateFlow<String>
    private var check = MutableLiveData<Boolean>(true)
    val books: LiveData<Boolean>
        get() = check

    override suspend fun checkRegisterMobileNumber(
        countryCode: String,
        mobNum: String,
        userType: String,
        navController: NavController?,
        context: Context
    ): Flow<Resource<CheckMobileNumberResponse>> = flow<Resource<CheckMobileNumberResponse>> {
        try {
            val obj = CheckMobileNumberRequest(countryCode, mobNum, userType)
            userDataSource.checkMobileNumber(obj).run {
                if (!this.isSuccessful) {
                    Log.e("AuthRepoImpl", "isNNNNotSuccessful")
                    Log.e("AuthRepoImpl", message)
//                    Toast.makeText(this,"dsadasasa",Toast.LENGTH_SHORT).show()

                    emit(
                        Resource.error(
                            ApiFailureException(
                                message = this.message(), code = this.code()
                            )
                        )
                    )

                }
                if (this.isSuccessful) {
                    Log.e("AuthRepoImpl", "isSuccessful " + this.body())

//                    firebaseAuth(number=mobNum,navController=navController,context=context)
//                    loginViewModel?.firebaseAuth(navController, context, loginViewModel.phoneNumber)
                    loginViewModel?.firebaseAuth(navController, context, mobNum)
                    emit(
                        Resource.success(this.body())
                    )
                }
            }
        } catch (e: BusAlertException) {
            emit(ErrorHandler.handleException(e as Exception))
        }
    }.applyCommonSideEffects().catch {
        emit(handleException(it as Exception))
    }

    override suspend fun registerUser(userData: UserData): ResourceBus<UserRegisterResponse> {
        return try {
            val response = userDataSource.registerUser(userData)
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
    override suspend fun getDriveDetails(id: String): ResourceBus<Profile> {
        return try {
            val response = userDataSource.getProfile(id)
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun getRouteList(branchId: String?): ResourceBus<List<RouteListResponse>> {
        return try {
            val response = userDataSource.getRouteList(branchId, false,"id,name,type")
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}




