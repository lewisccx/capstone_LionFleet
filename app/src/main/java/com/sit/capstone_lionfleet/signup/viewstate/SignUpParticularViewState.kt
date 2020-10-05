package com.continental.cityfleet.signup.viewstates

data class SignUpParticularViewState(
    val isFirstNameInputValid: Boolean = true,
    val isLastNameInputValid: Boolean = true,
    val isCountryCodeInputValid: Boolean = true,
    val isPhoneNumberInputValid: Boolean = true,
    val isBtnNextEnabled: Boolean = false
)
