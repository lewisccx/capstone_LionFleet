package com.sit.capstone_lionfleet.business.profile

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.core.di.PreferenceProvider
import com.sit.capstone_lionfleet.core.di.PreferenceProvider.Companion.CURRENT_LOGGED_IN_USER_ID_KEY
import com.sit.capstone_lionfleet.login.ui.LoginActivity
import com.sit.capstone_lionfleet.business.profile.network.model.User
import com.sit.capstone_lionfleet.utils.startNewActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel
@ViewModelInject
constructor(
    private val repository: ProfileRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModel() {

    private val _profileDataState: MutableLiveData<Resource<User>> = MutableLiveData()
    val profileDataState: LiveData<Resource<User>>
        get() = _profileDataState

    fun setProfileStateEvent(profileStateEvent: ProfileStateEvent, activity: Activity) {
        viewModelScope.launch {
            when (profileStateEvent) {
                is ProfileStateEvent.getProfile -> {
                    retrieveUserProfile()
                }
                is ProfileStateEvent.signOut-> {
                    signOut(activity)
                }

            }
        }
    }

    private suspend fun retrieveUserProfile() {
        repository.getProfile().onEach {
            _profileDataState.value = it
        }.launchIn(viewModelScope)
    }
    private fun switchToLogin(activity: Activity) {
        activity.startNewActivity(LoginActivity::class.java)
    }
    private  fun signOut(activity: Activity){
        preferenceProvider.deleteValueByKey(CURRENT_LOGGED_IN_USER_ID_KEY)
        switchToLogin(activity)
    }
}
