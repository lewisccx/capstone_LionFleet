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
        const val PWD_RESET_KEY = "PWD_RESET_KEY"
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

    fun saveAsPref(key: String, value: String){
        preference.edit().putString(
            key,value
        ).apply()
    }
    fun geValueFromPref(key: String):String?{
        return preference.getString(key, null)
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