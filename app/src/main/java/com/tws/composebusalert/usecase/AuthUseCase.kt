package com.tws.composebusalert.usecase

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tws.composebusalert.nav.LoginType
import com.tws.composebusalert.preference.PREF_DRIVER_CODE
import com.tws.composebusalert.preference.PREF_DRIVER_PROFILE_ID
import com.tws.composebusalert.preference.PreferenceManager
import com.tws.composebusalert.repo.AuthorizationRepo
import com.tws.composebusalert.request.UserData
import com.tws.composebusalert.responses.CheckMobileNumberResponse
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.responses.UserRegisterResponse
import com.tws.composebusalert.services.ApiFailureException
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TYPE_CARE_TAKER = "careTaker"
private const val TYPE_DRIVER = "driver"

class AuthUseCase @Inject constructor(
    private val authorizationRepo: AuthorizationRepo,
//    private val preferenceManager: PreferenceManager
    ) {
    private fun logout() {
//   LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
    }
    //    val loginViewModel= DriverLoginViewModel()
    val loginViewModel: DriverLoginViewModel? = null
    suspend fun checkRegisterMobileNumber(
        countryCode: String,
        mobNum: String,
        userType: String,
        navController: NavController? = null,
        context: Context
    ): Flow<Resource<CheckMobileNumberResponse>> {
        return authorizationRepo.checkRegisterMobileNumber(
            countryCode,
            mobNum,
            userType,
            navController,
            context
        )
    }
    /**
     * this method is triggered after fire base sign in to register the user in server with social login id
     * @param [user] is the social user profile after Fire base sign in
     * */
    @Throws(ApiFailureException::class)
    suspend fun registerUserToServer(
        user: FirebaseUser,
        loginType: LoginType,
        number: String,

        ): UserRegisterResponse? {
        var countryCode: String? = null
        var phNo: String? = null
        if (loginType == LoginType.PHONE_NUMBER) {
            countryCode = "+91"
//            phNo = user.phoneNumber
            phNo = number
//            phNo = "8870068009"
//            phNo = loginViewModel?.phoneNumber

        }
        authorizationRepo.registerUser(
            UserData(
                user.email,
                user.displayName,
                "",
                TYPE_DRIVER,
//                googleId,
//                facebookId,
                phNo,
                countryCode
            )
        ).apply {
            return when (this.status) {
                Status.SUCCESS -> {
                    this.data?.let {
//                         storeDataInPreference(it)
                        Log.e(
                            "Auth UseCase",
                            " RESPONSENEW Auth UseCase ${it.user.phoneNumber}   ${it.user.email}  ${it.caretaker}   ${it.user.id}"
                        )
                        Log.e(
                            "Auth UseCase AAAAAAA ",
                            "${it.token}   ${it.user}  ${it.caretaker} "
                        )
//                            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()

                        it
                    }
                }
                Status.ERROR -> {
//                        logout()
                    Log.e("Auth UseCase", "Failed")

//                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    throw ApiFailureException(this.message)
                }
                else -> {
                    logout()
                    null
                }
            }
        }
    }


    @Throws(ApiFailureException::class)
    suspend fun getDriverDetails(): Profile? {
//         preferenceManager.getValue(PREF_DRIVER_PROFILE_ID)?.let {
//        authorizationRepo.getDriveDetails("driver_profile_id").apply {
        authorizationRepo.getDriveDetails("d2b5eed8-96a1-4003-8e7b-6571767e969c").apply {
            Log.e("AuthUseCase", "DriverDetail Profile")
            return when (this.status) {
                Status.SUCCESS -> this.data
                Status.ERROR -> throw ApiFailureException(this.message)
                else -> null
            }
//             }
        }
        return null
    }

  /*  private fun storePreferenceData(
        driverCode: String,
        profileId: String?,
        authToken: String?
    ) {
        preferenceManager.storeValue(PREF_DRIVER_CODE, driverCode)
        profileId?.let { preferenceManager.storeValue(PREF_DRIVER_PROFILE_ID, it) }
        authToken?.let { preferenceManager.storeAndUpdateAccessToken(it) }
    }
*/

}

