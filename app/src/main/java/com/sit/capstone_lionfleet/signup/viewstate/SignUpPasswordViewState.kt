package com.continental.cityfleet.signup.viewstates

data class SignUpPasswordViewState(
    val isPasswordInput8MinCharacterValid: Boolean = true,
    val isPasswordInputAtLeast1NumberValid: Boolean = true,
    val isPasswordInputAtLeast1UpperCaseValid: Boolean = true,
    val isPasswordInputAtLeast1LowerCaseValid: Boolean = true,
    val isPasswordInputAtLeast1SpecialCharacterValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isBtnNextEnabled: Boolean = false
)
