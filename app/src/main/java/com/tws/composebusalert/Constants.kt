package com.tws.composebusalert

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Patterns
import java.util.*
import java.util.regex.Pattern

const val NO_INTERNET_ERROR_CODE = 503
const val IMAGE_CHOOSE = 1
const val PERMISSION_CODE = 2
const val IMAGE = "image/*"
const val SERVER_ERROR = 500

var FCM_TOKEN: String = ""

const val NAV_HOME = "Home"
const val NAV_APPOINTMENT = "Appointments"
const val NAV_NOTIFICATION = "Notifications"
const val NAV_RESOURCE = "Resources"
const val NAV_PAST_E_VISITS = "Past e-visits"
const val NAV_INBOX = "Inbox"
const val NAV_MY_ACCOUNT = "My Account"
const val NAV_BILLING = "Billing"
const val NAV_SETTINGS = "Settings"
const val NAV_SIGN_OUT = "Signout"
const val BOOK_APPOINTMENT = "Book Appointment"
const val SCHEDULE_APPOINTMENT = "Schedule Appointment"
const val ADD_MEMBER_DETAILS = "Add Member Details"
const val TITLE = "Title"
const val DESCRIPTION = "Description"
const val SPECIALITY_ID = "Speciality Id"
const val SPECIALITY_GROUP_ID = "Speciality Group Id"
const val DOCTOR_PROFILE = "Doctor Profile"
const val HOSPITAL_DETAILS = "Hospital Details"
const val PHARMACIES_DETAILS = "Pharmacies Details"
const val LABORATORY_DETAILS = "Laboratory Details"
const val RADIOLOGY_DETAILS = "Radiology Details"
const val IS_EDIT = "IsEdit"
const val SELECTED_SLOT_TIME = "SelectedSlotTime"
const val DOCTOR_FEE = "Doctor fee"
const val PATIENT = "patient"
const val DOCTOR_CALENDAR_ID = "doctorCalendarId"
var IS_ARABIC = false
const val TERMS_CONDITIONS_URL = "https://doctors.istishartak.com/auth/doctor-signup"


fun isValidMobile(phone: String): Boolean {
    return if (!Pattern.matches("[a-zA-Z]+", phone)) {
        phone.length in 7..13
    } else false
}

fun isValidPassword(password: String): Boolean {
    return if (!Pattern.matches("[a-zA-Z]+", password)) {
        password.length >= 8
    } else false
}

fun isValidEmail(email: String?): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
}


class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {

        fun updateLocale(context: Context, localeToSwitchTo: Locale): ContextWrapper {
            var thisContext = context
            val resources: Resources = context.resources
            val configuration: Configuration = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(localeToSwitchTo)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.locale = localeToSwitchTo
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                thisContext = context.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            return ContextUtils(thisContext)
        }
    }
}


enum class AddProfile {
    SELF_PROFILE,
    FAMILY_PROFILE
}



