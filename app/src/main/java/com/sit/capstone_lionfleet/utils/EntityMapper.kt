package com.sit.capstone_lionfleet.utils

import com.sit.capstone_lionfleet.base.response.Resource

interface EntityMapper<Entity, DomainModel> {

    fun mapFromEntity(entity: Entity) : DomainModel
    //fun mapToResponse(domainModel: DomainModel): Entity
}