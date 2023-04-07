package com.tws.composebusalert.di

 const val SERVER_URL = "http://206.189.137.65"
const val TIME_OUT = 60L

class Settings{

    var token: String? = null
        get() = field

    var pickupId: String? = null
        get() = field

    var dropId: String? = null
        get() = field

    var branchId: String? = null
        get() = field

    var phoneNo: String? = null
        get() = field
}