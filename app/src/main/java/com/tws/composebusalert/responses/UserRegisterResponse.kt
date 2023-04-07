package com.tws.composebusalert.responses

import com.google.gson.annotations.SerializedName
import com.tws.composebusalert.responses.Caretaker
import com.tws.composebusalert.responses.User

/**
 * User register response class to parse json data
 * */
class UserRegisterResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("caretaker")
    val caretaker: Caretaker?
)