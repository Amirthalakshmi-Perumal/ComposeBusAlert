package com.tws.composebusalert.repo

import android.content.Context
import android.provider.ContactsContract

import androidx.navigation.NavController
import com.tws.composebusalert.request.UserData
import com.tws.composebusalert.responses.CheckMobileNumberResponse
import com.tws.composebusalert.responses.Profile
import com.tws.composebusalert.responses.UserRegisterResponse
import com.tws.composebusalert.services.Resource
import com.tws.composebusalert.services.ResourceBus
import kotlinx.coroutines.flow.Flow

interface AuthorizationRepo {
    suspend fun checkRegisterMobileNumber(
        countryCode: String,
        mobNum: String,
        userType: String,
        navController: NavController? = null,
        context: Context
    ) : Flow<Resource<CheckMobileNumberResponse>>

    suspend fun registerUser(userData: UserData): ResourceBus<UserRegisterResponse>
    suspend fun getDriveDetails(id: String): ResourceBus<Profile>
//    suspend fun firebaseAuth():FirebaseAuth


   /* suspend fun checkMobile(
        checkMobileNumberRequest: CheckMobileNumberRequest
    ): Resource<CheckMobileNumberResponse>*/
}