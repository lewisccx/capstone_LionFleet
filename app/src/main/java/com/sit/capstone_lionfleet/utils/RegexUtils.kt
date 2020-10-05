package com.continental.cityfleet.utils

import java.util.regex.Pattern

@Suppress("TooManyFunctions")
class RegexUtils {

    companion object {

        private const val EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
        private const val PASSWORD_EIGHT_CHAR = "^.{8,}$"
        private const val PASSWORD_ONE_NUMBER = "^.*[0-9].*$"
        private const val PASSWORD_ONE_UPPER_CASE = "^.*[A-Z].*$"
        private const val PASSWORD_ONE_LOWER_CASE = "^.*[a-z].*$"
        private const val PASSWORD_ONE_SPECIAL_CHAR =
            "^.*[\\^\$*.\\[\\]{}()?\\-\"!@#%&/,><':;|_~`+=].*"
        private const val PASSWORD_VALID = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\^\$*.\\[\\]" +
                "{}()?\\-\"!@#%&/,><':;|_~])[a-zA-Z0-9^\$*.\\[\\]{}()?\\-\"!@#%&/,><':;|_~`+=]{8,}$"

        private const val NAME_REGEX = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁ" +
                "ÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð\\s,.'\\-]{2,50}$"

        private const val CNTRY_CODE_REGEX = "^[+](\\+?\\d{1,3}|\\d{1,4})\$"
        private const val PHONE_REGEX = "^[0-9]{8,}$"

        private const val STREET_REGEX = "^.{1,150}\$"
        private const val STREET_NUMBER_REGEX = "^[0-9]+\$"
        private const val POST_CODE_REGEX = "^[a-zA-Z0-9\\s]{2,10}\$"
        private const val CITY_REGEX = "^[a-zA-Z0-9àáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçč" +
                "šžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð\\s,.&()/;'\\-]{2,50}$"

        fun isEmailValid(input: String): Boolean {
            return Pattern.compile(EMAIL_REGEX).matcher(input).matches()
        }

        fun isMin8Char(password: String): Boolean {
            return Pattern.compile(PASSWORD_EIGHT_CHAR).matcher(password).matches()
        }

        fun isMin1Number(password: String): Boolean {
            return Pattern.compile(PASSWORD_ONE_NUMBER).matcher(password).matches()
        }

        fun isMin1UpperCase(password: String): Boolean {
            return Pattern.compile(PASSWORD_ONE_UPPER_CASE).matcher(password).matches()
        }

        fun isMin1LowerCase(password: String): Boolean {
            return Pattern.compile(PASSWORD_ONE_LOWER_CASE).matcher(password).matches()
        }

        fun isMin1SpecialChar(password: String): Boolean {
            return Pattern.compile(PASSWORD_ONE_SPECIAL_CHAR).matcher(password).matches()
        }

        fun isPasswordValid(password: String): Boolean {
            return Pattern.compile(PASSWORD_VALID).matcher(password).matches()
        }

        fun isNameValid(name: String): Boolean {
            return Pattern.compile(NAME_REGEX).matcher(name).matches()
        }

        fun isCountryCodeValid(countryCode: String): Boolean {
            return Pattern.compile(CNTRY_CODE_REGEX).matcher(countryCode).matches()
        }

        fun isPhoneNumberValid(phone: String): Boolean {
            return Pattern.compile(PHONE_REGEX).matcher(phone).matches()
        }

        fun isStreetValid(street: String): Boolean {
            return Pattern.compile(STREET_REGEX).matcher(street).matches()
        }

        fun isStreetNumberValid(streetNumber: String): Boolean {
            return Pattern.compile(STREET_NUMBER_REGEX).matcher(streetNumber).matches()
        }

        fun isPostCodeValid(postCode: String): Boolean {
            return Pattern.compile(POST_CODE_REGEX).matcher(postCode).matches()
        }

        fun isCityValid(city: String): Boolean {
            return Pattern.compile(CITY_REGEX).matcher(city).matches()
        }
    }
}
