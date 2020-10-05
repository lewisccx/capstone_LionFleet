package com.continental.cityfleet.signup.viewstates

data class SignUpAddressViewState(
    val isCityInputValid: Boolean = true,
    val isStreetNumberInputValid: Boolean = true,
    val isStreetInputValid: Boolean = true,
    val isPostCodeInputValid: Boolean = true,
    val isBtnNextEnabled: Boolean = false
)
