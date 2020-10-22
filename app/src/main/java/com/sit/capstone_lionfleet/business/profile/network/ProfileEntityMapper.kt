package com.sit.capstone_lionfleet.business.profile.network

import com.sit.capstone_lionfleet.business.profile.network.response.ProfileResponse

import com.sit.capstone_lionfleet.business.profile.network.model.User

import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class ProfileEntityMapper
@Inject
constructor() : EntityMapper<ProfileResponse, User> {
    override fun mapFromEntity(entity: ProfileResponse): User {
        return User(
            postcode = entity.user.address.postcode,
            street = entity.user.address.street,
            streetNumber = entity.user.address.streetNumber,
            city = entity.user.address.city,
            countryCode = entity.user.phone.countryCode,
            phoneNumber = entity.user.phone.phoneNumber,
            email = entity.user.email,
            firstName = entity.user.firstName,
            lastName = entity.user.lastName,
            isActivated = entity.user.isActivated,
            licenseActivated = entity.user.licenseActivated
        )
    }
}