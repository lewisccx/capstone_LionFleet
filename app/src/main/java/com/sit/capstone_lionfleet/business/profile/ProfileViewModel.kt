package com.sit.capstone_lionfleet.business.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.profile.network.response.ProfileResponse
import com.sit.capstone_lionfleet.profile.network.model.User
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel
@ViewModelInject
constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profileDataState: MutableLiveData<Resource<User>> = MutableLiveData()
    val profileDataState: LiveData<Resource<User>>
        get() = _profileDataState

    fun setProfileStateEvent(profileStateEvent: ProfileStateEvent) {
        viewModelScope.launch {
            when (profileStateEvent) {
                is ProfileStateEvent.getProfile -> {
                    retrieveUserProfile()
                }

            }
        }
    }

    private suspend fun retrieveUserProfile() {
        repository.getProfile().onEach {
            _profileDataState.value = it
        }.launchIn(viewModelScope)
    }
}
