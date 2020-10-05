package com.sit.capstone_lionfleet.utils

sealed class DataState<out R> {
    data class Success<out T>(val data: T ): DataState<T>()
    data class Error<out T>(val msg: String, val data: T): DataState<T>()
    object Loading: DataState<Nothing>()
}