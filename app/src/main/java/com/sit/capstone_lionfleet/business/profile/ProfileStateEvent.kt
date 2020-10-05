package com.sit.capstone_lionfleet.business.profile

sealed class ProfileStateEvent {
    object getProfile : ProfileStateEvent()
    object signOut : ProfileStateEvent()
}