package com.sit.capstone_lionfleet.login.viewstate

sealed class LoginStateEvent {
    object LoginEvent: LoginStateEvent()
    object None: LoginStateEvent()
}