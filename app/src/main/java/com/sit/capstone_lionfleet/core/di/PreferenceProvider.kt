package com.sit.capstone_lionfleet.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceProvider
@Inject
constructor(@ApplicationContext private val context: Context) {
    companion object {
        const val AUTH_KEY = "SESSION_KEY"
        const val CURRENT_LOGGED_IN_USER_ID_KEY = "CURRENT_LOGGED_IN_USER_ID"
        const val PWD_RESET_KEY = "PWD_RESET_KEY"
        const val SELECTED_VEHICLE_ID_KEY = "SELECTED_VEHICLE_ID_KEY"
        const val CAR_LOCK = "CAR_LOCK"
    }

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)


    fun saveAuthToken(token: String?) {
        preference.edit().putString(
            AUTH_KEY,
            token
        ).apply()
    }

    fun getAuthToken(): String? {
        return preference.getString(AUTH_KEY, null)
    }

    fun saveLoggedInUserId(userId: String?) {
        preference.edit().putString(
            CURRENT_LOGGED_IN_USER_ID_KEY,
            userId
        ).apply()
    }

    fun getLoggedInUserId(): String? {
        return preference.getString(CURRENT_LOGGED_IN_USER_ID_KEY, null)
    }

    fun saveSelectedVehicleIdAsPref(vehicleId: String?) {
        preference.edit().putString(
            SELECTED_VEHICLE_ID_KEY,
            vehicleId
        ).apply()
    }

    fun getSelectedVehicleId(): String? {
        return preference.getString(
            SELECTED_VEHICLE_ID_KEY, null
        )
    }

    fun saveAsPref(key: String, value: String) {
        preference.edit().putString(
            key, value
        ).apply()
    }

    fun geValueFromPref(key: String): String? {
        return preference.getString(key, null)
    }

    fun deleteValueByKey(key: String){
        preference.edit().remove(key).commit()
    }

    fun saveCarStatus(value: Boolean){
        preference.edit().putBoolean(
            CAR_LOCK, value
        ).apply()
    }

    fun getCarStatus(): Boolean{
        return preference.getBoolean(CAR_LOCK, true)
    }
    fun savePasswordResetToken(token: String?) {
        preference.edit().putString(
            PWD_RESET_KEY,
            token
        ).apply()
    }

    fun getPasswordResetToken(): String? {
        return preference.getString(PWD_RESET_KEY, null)
    }
}