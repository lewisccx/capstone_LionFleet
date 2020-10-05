package com.sit.capstone_lionfleet.login.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.BusinessActivity
import com.sit.capstone_lionfleet.forgotpassword.ForgotPasswordActivity
import com.sit.capstone_lionfleet.login.network.request.LoginRequest
import com.sit.capstone_lionfleet.login.network.response.LoginResponse
import com.sit.capstone_lionfleet.login.repository.LoginRepository
import com.sit.capstone_lionfleet.login.viewstate.LoginStateEvent
import com.sit.capstone_lionfleet.login.viewstate.LoginViewState
import com.sit.capstone_lionfleet.signup.ui.activity.RegisterActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel
@ViewModelInject
constructor(
    private val repository: LoginRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var password: String = ""
    private var email: String = ""


    private val _viewState = MutableLiveData<LoginViewState>().apply { LoginViewState() }
    val viewState: LiveData<LoginViewState>
        get() = _viewState

    private val _loginState: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginState: LiveData<Resource<LoginResponse>>
        get() = _loginState

    fun saveAuthToken(token: String) = viewModelScope.launch {
        repository.saveAuthToken(token)
    }

    fun switchToSignUp(activity: Activity) {
        val intent = Intent(activity, RegisterActivity::class.java)
        activity.startActivity(intent)
    }

    fun switchToBusiness(activity: Activity) {
        val intent = Intent(activity, BusinessActivity::class.java)
        activity.startActivity(intent)
    }

    private fun switchToForgotPassword(activity: Activity) {
        val intent = Intent(activity, ForgotPasswordActivity::class.java)
        activity.startActivity(intent)
    }

    fun setStateEvent(loginStateEvent: LoginStateEvent){
        viewModelScope.launch {
            when(loginStateEvent){
                is LoginStateEvent.LoginEvent -> {
                    login(email,password)
                }

            }
        }
    }

     suspend fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
         repository.login(loginRequest).collect {
             _loginState.value = it
        }
    }

    fun onPasswordInputChanged(password: String) {
        this.password = password
        _viewState.value =
            LoginViewState(loginButtonEnabled = password.isNotBlank() && password.isNotEmpty())
    }

    fun onEmailInputChanged(email: String) {
        this.email = email
        _viewState.value =
            LoginViewState(loginButtonEnabled = password.isNotBlank() && password.isNotEmpty())
    }
}