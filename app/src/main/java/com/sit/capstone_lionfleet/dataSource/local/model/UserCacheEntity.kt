package com.sit.capstone_lionfleet.dataSource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_USER_ID = 0
@Entity(tableName = "users")
data class UserCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val uid: Int = CURRENT_USER_ID,
    @ColumnInfo(name = "postcode")
    val postcode: String,
    @ColumnInfo(name = "street")
    val street: String,
    @ColumnInfo(name = "streetNumber")
    val streetNumber: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "firstName")
    val firstName: String,
    @ColumnInfo(name = "isActivated")
    val isActivated: Boolean,
    @ColumnInfo(name = "licenseActivated")
    val licenseActivated: Boolean,
    @ColumnInfo(name = "lastName")
    val lastName: String,
    @ColumnInfo(name = "countryCode")
    val countryCode: String,
    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String

)