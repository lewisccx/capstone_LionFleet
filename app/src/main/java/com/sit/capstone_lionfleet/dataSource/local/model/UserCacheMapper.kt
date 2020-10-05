package com.sit.capstone_lionfleet.dataSource.local.model


import com.sit.capstone_lionfleet.profile.network.model.User
import com.sit.capstone_lionfleet.utils.EntityMapper
import javax.inject.Inject

class UserCacheMapper
@Inject
constructor() : EntityMapper<UserCacheEntity, User> {
    override fun mapFromEntity(entity: UserCacheEntity): User {
        return User(
            postcode = entity.postcode,
            street = entity.street,
            streetNumber = entity.streetNumber,
            city = entity.city,
            countryCode = entity.countryCode,
            phoneNumber = entity.phoneNumber,
            email = entity.email,
            firstName = entity.firstName,
            lastName = entity.lastName,
            isActivated = entity.isActivated
        )
    }

    fun mapToEntity(domainModel: User): UserCacheEntity {
        return UserCacheEntity(
            postcode = domainModel.postcode,
            street = domainModel.street,
            streetNumber = domainModel.streetNumber,
            city = domainModel.city,
            countryCode = domainModel.countryCode,
            phoneNumber = domainModel.phoneNumber,
            email = domainModel.email,
            firstName = domainModel.firstName,
            lastName = domainModel.lastName,
            isActivated = domainModel.isActivated
        )
    }
}