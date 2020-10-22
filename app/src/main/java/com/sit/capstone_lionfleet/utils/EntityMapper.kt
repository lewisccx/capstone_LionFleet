package com.sit.capstone_lionfleet.utils

interface EntityMapper<Entity, DomainModel> {

    fun mapFromEntity(entity: Entity): DomainModel
    //fun mapToResponse(domainModel: DomainModel): Entity
}