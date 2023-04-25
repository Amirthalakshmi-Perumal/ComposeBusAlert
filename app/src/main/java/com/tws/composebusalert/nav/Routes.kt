package com.tws.composebusalert.nav

enum class Routes {

    Dashboard,
    Phone,
    OTP,
    DriverSelectRouteScreen,
    DriverDashboard,
    AlertScreen,
    MapScreen,
    PassengerList,
    BottomSheet,
    Summa
}
enum class LoginType {
    GOOGLE,
    FACE_BOOK,
    PHONE_NUMBER
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
object NavigationKeys {
    val MY_DATA = "my_data"
}
