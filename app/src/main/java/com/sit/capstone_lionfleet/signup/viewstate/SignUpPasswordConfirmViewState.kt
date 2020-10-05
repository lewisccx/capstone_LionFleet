package com.continental.cityfleet.signup.viewstates

data class SignUpPasswordConfirmViewState(
    val isConfirmPasswordInputValid: Boolean = true,
    val isBtnNextEnabled: Boolean = false
)
